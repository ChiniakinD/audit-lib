package ru.chiniakin.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.chiniakin.model.kafka.LogModel;

@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
public class LogConverter {

    @SuppressWarnings("checkstyle:ConstantName")
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public String convertLogModelToString(LogModel logModel) throws JsonProcessingException {
        return objectMapper.writeValueAsString(logModel);
    }

}
