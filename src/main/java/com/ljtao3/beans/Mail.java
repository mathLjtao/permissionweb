package com.ljtao3.beans;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Mail {
    private String subject;
    private String message;
    private Set<String> receivers;

}
