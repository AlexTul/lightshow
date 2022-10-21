package com.nixsolutions.alextuleninov.modulthird.command;

import com.nixsolutions.alextuleninov.modulthird.command.data.CreateInputRequest;
import com.nixsolutions.alextuleninov.modulthird.model.ColorHistoryRecord;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Map;

public class CommandFactory {

    private final SessionFactory sessionFactory;

    public CommandFactory(SessionFactory session) {
        this.sessionFactory = session;
    }

    public Command<Map<String, List<ColorHistoryRecord>>> lightshowInput(CreateInputRequest context) {
        return new LightshowHistoryCommand(sessionFactory, context);
    }

}
