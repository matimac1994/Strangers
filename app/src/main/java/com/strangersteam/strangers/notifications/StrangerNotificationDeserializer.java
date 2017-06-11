package com.strangersteam.strangers.notifications;

import android.util.Log;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by kroli on 11.06.2017.
 */

class StrangerNotificationDeserializer extends JsonDeserializer<StrangerNotification<?>> {
    @Override
    public StrangerNotification<?> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        ObjectMapper mapper = (ObjectMapper)p.getCodec();
        TreeNode node = mapper.readTree(p);
        String notificationTypeString = ((JsonNode)node.get("notificationType")).asText();
        NotificationType notificationType = NotificationType.valueOf(notificationTypeString);
        String notificationContentString = node.get("notificationContent").toString();
        NotificationContent notificationContent = mapper.readValue(notificationContentString, getTypeReference(notificationType));

        return new StrangerNotification<>(notificationType,notificationContent);
    }

    private TypeReference getTypeReference(NotificationType notificationType) {
        switch (notificationType){
            case ONE_EVENT_MSG:
                return  new TypeReference<OneMsgNotificationContent>() {};
            case FEW_EVENTS_MSG:
                return new TypeReference<FewEventsMsgNotificationContent>() {};
            case FEW_MY_EVENTS_MSG:
                return new TypeReference<FewEventsMsgNotificationContent>() {};
            default:
                Log.e(getClass().getSimpleName(),"bad NotificationType " + notificationType.name());
                return new TypeReference<Object>() {};
        }
    }
}
