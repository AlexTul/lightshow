package com.nixsolutions.alextuleninov.modulthird.cli;

import com.nixsolutions.alextuleninov.modulthird.command.Command;
import com.nixsolutions.alextuleninov.modulthird.command.CommandFactory;
import com.nixsolutions.alextuleninov.modulthird.command.data.CreateInputRequest;
import com.nixsolutions.alextuleninov.modulthird.exceptions.LightshowException;
import com.nixsolutions.alextuleninov.modulthird.model.ColorHistoryRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class LightshowInputInteractiveCLI {

    private static final Logger log = LoggerFactory.getLogger(LightshowInputInteractiveCLI.class);

    private final CommandFactory commandFactory;

    List<String> colorList;

    public LightshowInputInteractiveCLI(CommandFactory commandFactory) {
        this.commandFactory = commandFactory;
        colorList = new ArrayList<>();
    }

    public void run() throws LightshowException {

        var scanner = new Scanner(System.in);

        System.out.println("Enter light's label:");
        String label = scanner.nextLine();

        System.out.println("Enter color's list as like " +
                "'white black brown red orange yellow green blue purple grey':");
        String consoleColorList = scanner.nextLine();
        colorList = Arrays.asList(consoleColorList.split(" "));

        System.out.println("Enter interval between switching (in seconds):");
        long switchingInterval = scanner.nextLong();
        scanner.nextLine();

        System.out.println("Enter number of switches:");
        long numberOfSwitching = scanner.nextLong();
        scanner.nextLine();

        Command<Map<String, List<ColorHistoryRecord>>> command = commandFactory.lightshowInput(new CreateInputRequest(
                label,
                colorList,
                switchingInterval,
                numberOfSwitching
        ));

        Map<String, List<ColorHistoryRecord>> input = command.execute();

        printColorHistoryRecord(input);

        StringBuilder resultOutput = getInputResultString(input);
        System.out.println(resultOutput);

    }

    // to do InputDTO
    // 6.	Вивести всю історію переключень в форматі
    // (Light ‘my light’ changed color ‘red’ => ‘yellow’ => ‘blue’ => ‘white’ => ‘red’)
    private static StringBuilder getInputResultString(Map<String, List<ColorHistoryRecord>> input) {

        Set<String> keySetLabel = input.keySet();
        String keyLabel = keySetLabel.stream()
                .findFirst()
                .orElse(null);

        List<ColorHistoryRecord> colorHistoryRecords = input.get(keyLabel);

        StringBuilder result = new StringBuilder("Light '");
        result.append(keyLabel).append("' changed color '").append(colorHistoryRecords.get(0).getOldColor().getName());

        for (ColorHistoryRecord in : colorHistoryRecords) {
            result.append("' => '").append(in.getNewColor().getName());
        }
        result.append("'\n");

        return result;
    }

    private static void printColorHistoryRecord(Map<String, List<ColorHistoryRecord>> input) {

        Set<String> keySetLabel = input.keySet();
        String keyLabel = keySetLabel.stream()
                .findFirst()
                .orElse(null);

        for (ColorHistoryRecord c : input.get(keyLabel)) {
            log.info("Light '{}' changed color from '{}' to '{}' at {}",
                    c.getLight().getLabel(),
                    c.getOldColor().getName(),
                    c.getNewColor().getName(),
                    c.getChangedAt());
        }
    }

}
