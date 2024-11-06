package com.auth.auth.config;

import com.auth.auth.dto.SignupDTO;
import com.auth.auth.enums.UserRole;
import com.auth.auth.model.User;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // Register all mappings
        configureUserMappings(modelMapper);

        return modelMapper;
    }

    private void configureUserMappings(ModelMapper modelMapper) {
        //SignupDTO -> User
        modelMapper.addMappings(new PropertyMap<SignupDTO, User>() {
            @Override
            protected void configure() {
                map().setRole(UserRole.STANDARD_USER);
            }
        });
    }
}
