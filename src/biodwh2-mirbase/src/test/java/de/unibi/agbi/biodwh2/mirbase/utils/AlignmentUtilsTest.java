package de.unibi.agbi.biodwh2.mirbase.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AlignmentUtilsTest {
    @Test
    void getFoldStringFromHairpinAlignment() {
        final String alignment = "-     uuu     c            u   -        --   u \n" +
                                 " ccgag   caguu auguaaacaucc aca cucagcug  uca a\n" +
                                 " |||||   ||||| |||||||||||| ||| ||||||||  |||  \n" +
                                 " gguuc   gucga ugcauuuguagg ugu gggucggu  agu c\n" +
                                 "a     --u     c            -   a        ug   a ";
        assertEquals("(((((...(((((.((((((((((((.((((((((((((((....)))..)))))))).))))))))))))))).))))).))))).",
                     AlignmentUtils.getFoldStringFromHairpinAlignment(alignment));
    }
}