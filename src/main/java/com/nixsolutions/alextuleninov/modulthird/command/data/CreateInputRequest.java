package com.nixsolutions.alextuleninov.modulthird.command.data;

import java.util.List;

public record CreateInputRequest(

        String label,

        List<String> colors,

        Long switchingInterval,

        Long numberOfSwitching

) {
}
