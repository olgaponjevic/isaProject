package com.example.isaProtobufServer.controller;

import com.example.isaProtobufServer.dto.MessageRequest;
import com.example.isaProtobufServer.dto.MessageResponse;
import com.example.isaProtobufServer.proto.MessageProto;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class MessageController {

    @PostMapping("/json")
    public ResponseEntity<MessageResponse> handleJson(@RequestBody MessageRequest request) {
        MessageResponse response = new MessageResponse(
                request.getId(),
                "Echo: " + request.getContent(),
                System.currentTimeMillis(),
                "JSON server"
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/protobuf",
            consumes = "application/x-protobuf",
            produces = "application/x-protobuf")
    public ResponseEntity<byte[]> handleProtobuf(@RequestBody byte[] requestBytes) throws Exception {
        MessageProto.Request request = MessageProto.Request.parseFrom(requestBytes);

        MessageProto.Response response = MessageProto.Response.newBuilder()
                .setId(request.getId())
                .setContent("Echo: " + request.getContent())
                .setTimestamp(System.currentTimeMillis())
                .setServerInfo("Protobuf server")
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/x-protobuf"))
                .body(response.toByteArray());
    }
}