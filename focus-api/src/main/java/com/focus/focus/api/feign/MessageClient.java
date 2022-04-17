package com.focus.focus.api.feign;

import com.focus.focus.api.dto.MessageInfoDto;
import com.focus.focus.api.util.LoginVal;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "messageClient",value="message-service")
public interface MessageClient {
    @PostMapping("/searchByKeywords")
    List<MessageInfoDto> searchByKeywords(@RequestParam("keywords") String keywords,@RequestBody LoginVal loginval);
}
