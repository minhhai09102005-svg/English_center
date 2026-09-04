package com.example.english_center.converter;

import com.example.english_center.builder.UserAccountSearchBuilder;
import com.example.english_center.enums.Gender;
import com.example.english_center.enums.Role;
import com.example.english_center.enums.UserStatus;
import com.example.english_center.utils.MapUtil;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SearchBuilderConverter {

    public UserAccountSearchBuilder toUserAccountSearchBuilder(Map<String, Object> params) {
        UserAccountSearchBuilder.Builder builder = new UserAccountSearchBuilder.Builder();
        builder.setKeyword(MapUtil.getObject(params, "keyword", String.class));
        builder.setRole(MapUtil.getObject(params, "role", Role.class));
        builder.setStatus(MapUtil.getObject(params, "status", UserStatus.class));
        builder.setGender(MapUtil.getObject(params, "gender", Gender.class));
        return builder.build();
    }
}
