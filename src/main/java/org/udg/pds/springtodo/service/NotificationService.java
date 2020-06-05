package org.udg.pds.springtodo.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.udg.pds.springtodo.entity.NotificationRequest;

public class NotificationService {
    private static NotificationService service;
    private NotificationService(){}

    public static NotificationService getInstance(){
        if(service == null)
            service = new NotificationService();

        System.out.println("NotificationService created");

        return service;
    }

    public String sendNotification(NotificationRequest request){
        Message message = Message.builder().setNotification(new Notification(request.title, request.body)).setToken(request.target).build();
        String response = null;
        try {
            response = FirebaseMessaging.getInstance().send(message);
            System.out.println("Message sent!");
        } catch (FirebaseMessagingException e) {
            System.out.println("Fail to send firebase notification");
        }

        return response;
    }
}
