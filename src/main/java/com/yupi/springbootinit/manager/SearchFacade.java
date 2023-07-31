package com.yupi.springbootinit.manager;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.datasource.*;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.exception.ThrowUtils;
import com.yupi.springbootinit.model.dto.post.PostQueryRequest;
import com.yupi.springbootinit.model.dto.search.SearchRequest;
import com.yupi.springbootinit.model.dto.user.UserQueryRequest;
import com.yupi.springbootinit.model.entity.Picture;
import com.yupi.springbootinit.model.enums.SearchTypeEnum;
import com.yupi.springbootinit.model.vo.PostVO;
import com.yupi.springbootinit.model.vo.SearchVO;
import com.yupi.springbootinit.model.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.CompletableFuture;

/**
 * 搜索门面
 *
 这段代码是一个名为SearchFacade的组件类，它提供了一个searchAll方法，用于执行搜索操作并返回搜索结果。

 该类使用了一些依赖注入（Dependency Injection）注解，其中包括：

 @Component: 表示该类是一个组件，由Spring进行管理。

 @Resource: 表示对其他组件或资源的引用。

 @Slf4j: 自动生成日志对象，可以通过log变量进行日志记录。

 SearchFacade类中的searchAll方法接受两个参数：SearchRequest searchRequest和HttpServletRequest request。SearchRequest是一个请求对象，包含了搜索相关的参数，例如搜索类型、搜索文本、分页信息等。HttpServletRequest用于获取HTTP请求的相关信息。

 方法内部首先从searchRequest中获取搜索类型type，然后使用SearchTypeEnum.getEnumByValue方法根据type获取对应的搜索类型枚举searchTypeEnum。如果type为空，会抛出参数错误的异常。

 接下来，根据不同的情况进行搜索操作。如果searchTypeEnum为空，表示搜索类型未知，那么会并行执行三个搜索任务：搜索用户、搜索帖子和搜索图片。这里使用了CompletableFuture.supplyAsync方法将每个搜索任务提交给线程池异步执行。然后使用CompletableFuture.allOf方法等待所有任务完成。完成后，从每个任务中获取搜索结果，并将结果封装到SearchVO对象中返回。

 如果searchTypeEnum不为空，表示搜索类型已知，那么根据搜索类型从DataSourceRegistry中获取相应的数据源（实现了DataSource接口），然后调用该数据源的doSearch方法执行搜索操作，并将搜索结果封装到SearchVO对象中返回。

 需要注意的是，如果出现异常，会记录日志并抛出业务异常。

 总的来说，这个SearchFacade类用于根据搜索请求执行搜索操作，并根据搜索类型调用相应的数据源进行搜索，最后返回搜索结果。
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@Component
@Slf4j
public class SearchFacade {

    @Resource
    private PostDataSource postDataSource;

    @Resource
    private UserDataSource userDataSource;

    @Resource
    private PictureDataSource pictureDataSource;

    @Resource
    private DataSourceRegistry dataSourceRegistry;

    // [编程学习交流圈](https://www.code-nav.cn/) 快速入门编程不走弯路！30+ 原创学习路线和专栏、500+ 编程学习指南、1000+ 编程精华文章、20T+ 编程资源汇总

    public SearchVO searchAll(@RequestBody SearchRequest searchRequest, HttpServletRequest request) {
        //先拿到这个type，
        String type = searchRequest.getType();
        //从SearchTypeEnum获取得到枚举值
        SearchTypeEnum searchTypeEnum = SearchTypeEnum.getEnumByValue(type);
        //对type判空处理，如果为空那么就返回异常
        ThrowUtils.throwIf(StringUtils.isBlank(type), ErrorCode.PARAMS_ERROR);
        String searchText = searchRequest.getSearchText();
        long current = searchRequest.getCurrent();
        long pageSize = searchRequest.getPageSize();
        // 搜索出所有数据
        if (searchTypeEnum == null) {
            CompletableFuture<Page<UserVO>> userTask = CompletableFuture.supplyAsync(() -> {
                UserQueryRequest userQueryRequest = new UserQueryRequest();
                userQueryRequest.setUserName(searchText);
                Page<UserVO> userVOPage = userDataSource.doSearch(searchText, current, pageSize);
                return userVOPage;
            });

            CompletableFuture<Page<PostVO>> postTask = CompletableFuture.supplyAsync(() -> {
                PostQueryRequest postQueryRequest = new PostQueryRequest();
                postQueryRequest.setSearchText(searchText);
                Page<PostVO> postVOPage = postDataSource.doSearch(searchText, current, pageSize);
                return postVOPage;
            });

            CompletableFuture<Page<Picture>> pictureTask = CompletableFuture.supplyAsync(() -> {
                Page<Picture> picturePage = pictureDataSource.doSearch(searchText, 1, 10);
                return picturePage;
            });

            CompletableFuture.allOf(userTask, postTask, pictureTask).join();
            try {
                Page<UserVO> userVOPage = userTask.get();
                Page<PostVO> postVOPage = postTask.get();
                Page<Picture> picturePage = pictureTask.get();
                SearchVO searchVO = new SearchVO();
                searchVO.setUserList(userVOPage.getRecords());
                searchVO.setPostList(postVOPage.getRecords());
                searchVO.setPictureList(picturePage.getRecords());
                return searchVO;
            } catch (Exception e) {
                log.error("查询异常", e);
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "查询异常");
            }
        } else {
            // 如果不为空，那么根据不同的类别搜出相应的数据
            SearchVO searchVO = new SearchVO();
            DataSource<?> dataSource = dataSourceRegistry.getDataSourceByType(type);
            Page<?> page = dataSource.doSearch(searchText, current, pageSize);
            searchVO.setDataList(page.getRecords());
            return searchVO;
        }
    }
}
