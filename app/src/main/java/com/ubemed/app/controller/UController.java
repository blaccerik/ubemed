package com.ubemed.app.controller;

import com.ubemed.app.model.User;
import com.ubemed.app.model.UserResponse;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class UController {

    @MessageMapping("/user")  //
    @SendTo("/topic/user")
    public UserResponse getUser(User user) {
        System.out.println("eee");
        return new UserResponse("Hi " + user.getName());
    }

//    @MessageMapping("/fleet/{fleetId}/driver/{driverId}")
//    @SendTo("/topic/fleet/{fleetId}")
//    public Simple simple(@DestinationVariable String fleetId, @DestinationVariable String driverId) {
//        return new Simple(fleetId, driverId);
//    }
}
