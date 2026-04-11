package com.example.bankcards.util;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class MaskUtil {

    public String mask(String number){

        if(number==null || number.isBlank()){
            return null;
        }
        String normalized = number.replaceAll("\\s+","");
        return "**** **** **** " + normalized.substring(normalized.length()-4);
    }
}
