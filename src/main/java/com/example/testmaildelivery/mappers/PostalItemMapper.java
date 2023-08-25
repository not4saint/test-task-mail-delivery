package com.example.testmaildelivery.mappers;

import com.example.testmaildelivery.dto.PostalItemRegistrationRequest;
import com.example.testmaildelivery.models.PostalItem;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.convention.NameTokenizers;
import org.modelmapper.convention.NamingConventions;
import org.springframework.stereotype.Component;

@Component
public class PostalItemMapper {
    private final ModelMapper modelMapper;

    public PostalItemMapper() {
        this.modelMapper = new ModelMapper();
        Configuration configuration = modelMapper.getConfiguration();
        configuration.setFieldAccessLevel(Configuration.AccessLevel.PUBLIC);
        configuration.setSourceNamingConvention(NamingConventions.JAVABEANS_ACCESSOR);
        configuration.setDestinationNamingConvention(NamingConventions.JAVABEANS_MUTATOR);
        configuration.setSourceNameTokenizer(NameTokenizers.CAMEL_CASE);
        configuration.setDestinationNameTokenizer(NameTokenizers.CAMEL_CASE);
        configuration.setMatchingStrategy(MatchingStrategies.STRICT);
    }

    public PostalItem toModel(PostalItemRegistrationRequest postalItemRegistrationRequest) {
        return modelMapper.map(postalItemRegistrationRequest, PostalItem.class);
    }
}
