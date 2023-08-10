package com.yupi.springbootinit.manager;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.yucongming.dev.client.YuCongMingClient;
import com.yupi.yucongming.dev.common.BaseResponse;
import com.yupi.yucongming.dev.model.DevChatRequest;
import com.yupi.yucongming.dev.model.DevChatResponse;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
/**
 * @author : wangshanjie
 * @date : 18:12 2023/8/4
 */
/**
 * 用于对接 AI 平台
 */

@Service
public class AiManager {
    @Resource
    private YuCongMingClient yuCongMingClient;
    /**
     * AI 对话
     *
     * @param modelId
     * @param message
     * @return
     */
    public String doChat(long modelId, String message) {
        DevChatRequest devChatRequest = new DevChatRequest();
        devChatRequest.setModelId(modelId);
        devChatRequest.setMessage(message);
        YuCongMingClient client = new YuCongMingClient("wel72bx1rdhglzo1p6rxx5wf5zkoi1g0","jfoyo0covmo3xbd1b6ou7aa88ioyvfzx");
        BaseResponse<DevChatResponse> response = client.doChat(devChatRequest);
        if (response == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI 响应错误");
        }
        return response.getData().getContent();

    }
}