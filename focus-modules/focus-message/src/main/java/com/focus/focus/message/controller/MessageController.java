package com.focus.focus.message.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import com.focus.focus.api.dto.MessageDto;
import com.focus.focus.api.dto.MessageInfoDto;
import com.focus.focus.api.oss.QiNiuService;
import com.focus.focus.api.util.LoginVal;
import com.focus.focus.api.util.ResponseCode;
import com.focus.focus.api.util.ResponseMsg;
import com.focus.focus.message.service.IMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//import com.focus.auth.common.model.LoginVal;

@RestController
@Slf4j
@RequestMapping
public class MessageController {
    @Autowired
    private QiNiuService qiNiuService;

    @Autowired
    private IMessageService messageService;

    // 发布讯息时上传的图片
    @RequestMapping("/uploadMultiPictures")
    public ResponseEntity<ResponseMsg> uploadMultiPictures(@RequestParam("fileList") MultipartFile[] multipartFiles) throws IOException {
        // 当没有上传图片，直接返回success
        if(ArrayUtil.isEmpty(multipartFiles)){
            return ResponseEntity.ok(new ResponseMsg(ResponseCode.MESSAGE_UPLOAD_MULTI_IMAGES_SUCCESS.getCode(),
                    ResponseCode.MESSAGE_UPLOAD_MULTI_IMAGES_SUCCESS.getMsg(),null));
        }
        // 图片上传后的url
        List<String> imagesPath = new ArrayList<>();
        // 向七牛云OSS上传图片
        for(MultipartFile multipartFile:multipartFiles){
            String imagePath = qiNiuService.saveImage(multipartFile);
            imagesPath.add(imagePath);
        }
        // 图片上传后url为空，意味着上传失败
        if(CollectionUtil.isEmpty(imagesPath)){
            return ResponseEntity.ok(new ResponseMsg(ResponseCode.MESSAGE_UPLOAD_MULTI_IMAGES_ERROR.getCode(),
                    ResponseCode.MESSAGE_UPLOAD_MULTI_IMAGES_ERROR.getMsg(),null));
        }
        // 逗号分割，path1,path2,...
        String images = imagesPath.stream().collect(Collectors.joining(","));
        Map<String,String> data = new HashMap<>();
        data.put("images",images);
        return ResponseEntity.ok(new ResponseMsg(ResponseCode.MESSAGE_UPLOAD_MULTI_IMAGES_SUCCESS.getCode(),
                ResponseCode.MESSAGE_UPLOAD_MULTI_IMAGES_SUCCESS.getMsg(),data));
    }

    // 发布讯息
    @RequestMapping("/publishMessage")
    public ResponseEntity<ResponseMsg> publishMessage(@RequestBody MessageDto messageDto){
        if(ObjectUtil.isEmpty(messageDto)){
            return ResponseEntity.ok(new ResponseMsg(ResponseCode.MESSAGE_PUBLISH_ERROR.getCode(),
                    ResponseCode.MESSAGE_PUBLISH_ERROR.getMsg(),null));
        }
        Boolean res = messageService.save(messageDto);
        if(!res){
            return ResponseEntity.ok(new ResponseMsg(ResponseCode.MESSAGE_PUBLISH_ERROR.getCode(),
                    ResponseCode.MESSAGE_PUBLISH_ERROR.getMsg(),null));
        }
        return ResponseEntity.ok(new ResponseMsg(ResponseCode.MESSAGE_PUBLISH_SUCCESS.getCode(),
                ResponseCode.MESSAGE_PUBLISH_SUCCESS.getMsg(),null));
    }

    // 展示讯息，目前仅是从数据库分页显示
    @GetMapping("/showMessage")
    public ResponseEntity<ResponseMsg> showMessages(@RequestParam("page") Integer page){
        List<MessageInfoDto> messageInfoDtoList = messageService.showMessage(page);
        if(CollectionUtil.isEmpty(messageInfoDtoList)){
            return ResponseEntity.ok(new ResponseMsg(ResponseCode.MESSAGE_SHOW_ERROR.getCode(),
                    ResponseCode.MESSAGE_SHOW_ERROR.getMsg(),null));
        }
        Map<String,List<MessageInfoDto>> data = new HashMap<>();
        data.put("messageInfoDtos",messageInfoDtoList);
        return ResponseEntity.ok(new ResponseMsg(ResponseCode.MESSAGE_SHOW_SUCCESS.getCode(),
                ResponseCode.MESSAGE_SHOW_SUCCESS.getMsg(),data));
    }

    // 搜索byKeywords，搜索推文，这里改POST是因为报错[后期修改，feign的缘故]
    @PostMapping("/searchByKeywords")
    public List<MessageInfoDto> searchByKeywords(@RequestParam("keywords") String keywords,@RequestBody LoginVal currentUser){
        // 根据keywords获取messageDto
        List<MessageDto> messageDtos = messageService.searchByKeywords(keywords);
        if(CollectionUtil.isEmpty(messageDtos))
            return null;
        // 根据messageDto获取messageInfoDto
        List<MessageInfoDto> msgInfoDtos = messageService.getMsgInfoDtos(messageDtos,currentUser);
        log.info("currentUser: [{}]",currentUser);
        return msgInfoDtos;
    }

    // 根据userId和pageNum返回对应的消息列表
    @GetMapping("/getMessagesByAuthorId")
    public ResponseEntity<ResponseMsg> getMessagesByAuthorId(@RequestParam("authorId") String authorId,
                                                      @RequestParam("pageNum") int pageNum){
        List<MessageInfoDto> msgInfoDtos = messageService.getMsgInfoDtosByAuthorId(authorId, pageNum);
        if(CollectionUtil.isEmpty(msgInfoDtos)){
            return ResponseEntity.ok(new ResponseMsg(ResponseCode.MESSAGE_OF_USER_ERROR.getCode(),
                    ResponseCode.MESSAGE_OF_USER_ERROR.getMsg(),null));
        }
        Map<String,List<MessageInfoDto>> data = new HashMap<>();
        data.put("msgInfoDtos",msgInfoDtos);
        return ResponseEntity.ok(new ResponseMsg(ResponseCode.MESSAGE_OF_USER_SUCCESS.getCode(),
                ResponseCode.MESSAGE_OF_USER_SUCCESS.getMsg(),data));
    }

    // 获取userId的置顶消息
    @GetMapping("/getPinnedMessage")
    public ResponseEntity<ResponseMsg> getPinnedMessage(@RequestParam("userId") String userId){
        MessageInfoDto pinnedMsgInfoDto = messageService.getPinnedMsgInfoDto(userId);
        if(ObjectUtil.isEmpty(pinnedMsgInfoDto))
            return ResponseEntity.ok(new ResponseMsg(ResponseCode.MESSAGE_PINNED_ERROR.getCode(),
                    ResponseCode.MESSAGE_PINNED_ERROR.getMsg(),null));
        else
            return ResponseEntity.ok(new ResponseMsg(ResponseCode.MESSAGE_PINNED_SUCCESS.getCode(),
                    ResponseCode.MESSAGE_PINNED_SUCCESS.getMsg(),pinnedMsgInfoDto));
    }

    // 删除消息
    @DeleteMapping("/deleteMessageById")
    public ResponseEntity<ResponseMsg> deleteMessageById(@RequestParam("messageId") Long messageId){
        Boolean deleteStatus = messageService.deleteMessageById(messageId);
        if(!deleteStatus)
            return ResponseEntity.ok(new ResponseMsg(ResponseCode.MESSAGE_DELETE_ERROR.getCode(),
                    ResponseCode.MESSAGE_DELETE_ERROR.getMsg(),false));
        return ResponseEntity.ok(new ResponseMsg(ResponseCode.MESSAGE_DELETE_SUCCESS.getCode(),
                ResponseCode.MESSAGE_DELETE_SUCCESS.getMsg(),true));
    }

    // 获取评论消息
    @GetMapping("/getReplies")
    public ResponseEntity<ResponseMsg> getReplies(@RequestParam("inReplyToAuthorId") String inReplyToAuthorId,
                                                  @RequestParam("conversationId")Long conversationId){
        List<MessageInfoDto> replies = messageService.getReplies(inReplyToAuthorId, conversationId);
        if(null == replies){
            return ResponseEntity.ok(new ResponseMsg(ResponseCode.MESSAGE_OF_REPLY_NONE.getCode(),
                    ResponseCode.MESSAGE_OF_REPLY_NONE.getMsg(),null));
        }
        Map<String,List<MessageInfoDto>> infoDtos = new HashMap<>();
        infoDtos.put("replies",replies);;
        return ResponseEntity.ok(new ResponseMsg(ResponseCode.MESSAGE_OF_REPLY_SUCCESS.getCode(),
                ResponseCode.MESSAGE_OF_REPLY_SUCCESS.getMsg(),infoDtos));
    }
}
