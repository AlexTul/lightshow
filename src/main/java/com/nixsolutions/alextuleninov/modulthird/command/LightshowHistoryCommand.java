package com.nixsolutions.alextuleninov.modulthird.command;

import com.nixsolutions.alextuleninov.modulthird.command.data.CreateInputRequest;
import com.nixsolutions.alextuleninov.modulthird.exceptions.LightshowException;
import com.nixsolutions.alextuleninov.modulthird.model.Color;
import com.nixsolutions.alextuleninov.modulthird.model.ColorHistoryRecord;
import com.nixsolutions.alextuleninov.modulthird.model.Light;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.hibernate.SessionFactory;

import java.sql.Timestamp;
import java.time.*;
import java.util.*;

public class LightshowHistoryCommand implements Command<Map<String, List<ColorHistoryRecord>>> {

    private final SessionFactory sessionFactory;

    private final CreateInputRequest context;

    public LightshowHistoryCommand(SessionFactory sessionFactory, CreateInputRequest context) {
        this.sessionFactory = sessionFactory;
        this.context = context;
    }

    @Override
    public Map<String, List<ColorHistoryRecord>> execute() throws LightshowException {

        EntityManager entityManager = null;

        try {
            entityManager = sessionFactory.createEntityManager();
            entityManager.getTransaction().begin();

            // -	Якщо у списку кольорів є неіснуючі - завершити виконання програми з помилкою
            TypedQuery<String> queryColorsFromColorsDB = entityManager.createQuery(
                    "select c.name from Color c", String.class);
            List<String> colorsFromColorsDB = queryColorsFromColorsDB.getResultList();
            if (!(colorsFromColorsDB.containsAll(context.colors()))) {
                throw new LightshowException("There are non-existent colors in the list like " +
                        "'white black brown red orange yellow green blue purple grey'");
            }

            // -	Якщо інтервал меньше 1, вийти з помилкою
            if (context.switchingInterval() < 1) {
                throw new LightshowException("The interval is less than 1");
            }

            // -	Якщо кількість переключень меньше 1, вийти з помилкою
            if (context.numberOfSwitching() < 1) {
                throw new LightshowException("The number of switches is less than 1");
            }

            // -	Якщо світильник із таким label не існує, створити новий Light із випадковим
            // початковим кольором зі списку
            // возьмем названия всех светильников из базы
            TypedQuery<String> queryLabelNameFromLights = entityManager.createQuery(
                    "select l.label from Light l", String.class);
            List<String> labelNameFromLights = queryLabelNameFromLights.getResultList();
            int index;
            if (!(labelNameFromLights.contains(context.label()))) {
                Light light = new Light();
                light.setLabel(context.label());
                index = new Random().nextInt(context.colors().size() + 1);

                Color c = new Color();
                c.setId((long) index);
                //c.setName(context.colors().get(index));

                light.setColor(c);
                light.setEnabled(false);

                entityManager.persist(light);
            }

            // Get Entity from Lights from DB for label
            TypedQuery<Light> queryEntityFromLightsForLabel = entityManager.createQuery(
                    "select l from Light l where l.label = ?1", Light.class);
            queryEntityFromLightsForLabel.setParameter(1, context.label());
            List<Light> entityFromLights = queryEntityFromLightsForLabel.getResultList();
            Light light = entityFromLights.get(0);

            // -	Якщо світильник існує, але він наразі є enabled - true -
            // завершити виконання програми з помилкою
            if (light.isEnabled()) {
                throw new LightshowException("The light exists, but it is currently enabled");
            }

            // Если светильник есть и он enabled - false
            // 1.	Зробити світильник enabled = true
            light.setEnabled(true);

            long count = context.numberOfSwitching();
            Color oldColor;  // !!!!!!!!!!!!!!!!!!
            String oldColorName;
            String newColorName;
            List<ColorHistoryRecord> colorHistoryRecord = new ArrayList<>();
            do {
                // 2.	Змінити його колір на випадковий зі списку (окрім поточного),
                // створивши відповідний запис в історії змін і записавши лог операції
                // в форматі (Light ‘my light’ changed color from ‘red’ to ‘yellow’ at {{ISO timestamp}})
                oldColor = light.getColor();
                oldColorName = light.getColor().getName();
                do {
                    index = new Random().nextInt(context.colors().size() + 1);
                    newColorName = context.colors().get(index);
                } while (newColorName.equals(oldColorName));
                light.getColor().setName(newColorName);

                Timestamp executionTime = Timestamp.from(LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC)); // DateLocalTime

                ColorHistoryRecord colorHistory = new ColorHistoryRecord();
                colorHistory.setId();
                colorHistory.setLight(light);
                colorHistory.setOldColor(oldColor);
                colorHistory.setNewColor(light.getColor());
                colorHistory.setChangedAt(executionTime);

                entityManager.persist(colorHistory);

                colorHistoryRecord.add(colorHistory);

                // 3.	Почекати заданий інтервал (Thread.sleep)
                // приймає значення в мс, тож привести введене значення * 1000
                Thread.sleep(context.switchingInterval() * 1000);

                count--;
            } while (count != 0);

            // 5.	Виставити enabled = false
            light.setEnabled(false);

            entityManager.getTransaction().commit();

            Map<String, List<ColorHistoryRecord>> collectionDTO = new LinkedHashMap<>();
            collectionDTO.put(context.label(), colorHistoryRecord);
            return collectionDTO;

        } catch (LightshowException e) {
            entityManager.getTransaction().rollback();
            throw e;
        } catch (Exception e) {
            if (entityManager != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new LightshowException(e);
        }
    }

}
