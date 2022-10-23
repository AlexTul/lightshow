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

            // Check light's label for empty or contain only whitespace code points
            // Check user list for non-existent colors
            // Check switching interval (must be >= 1)
            // Check number of switching (must be >= 1)
            checkColorIntervalSwitching(entityManager, context);

            // Get all Entity from ColorsDB
            TypedQuery<Color> queryEntityFromColorsDB = entityManager.createQuery(
                    "select c from Color c", Color.class);
            List<Color> allEntityFromColorsDB = queryEntityFromColorsDB.getResultList();

            // Create list Entity for Color from User request
            List<Color> listEntityForColorFromUserRequest =
                    createListEntityForColorFromUserRequest(allEntityFromColorsDB);

            // Create a new Light
            // Get the names of all light from the LightsDB
            TypedQuery<String> queryLabelNameFromLights = entityManager.createQuery(
                    "select l.label from Light l", String.class);
            List<String> labelNameFromLights = queryLabelNameFromLights.getResultList();

            int index;
            if (!(labelNameFromLights.contains(context.label()))) {
                index = new Random().nextInt(context.colors().size());

                var randomColorEntityFromUserRequest =
                        listEntityForColorFromUserRequest.get(index);

                Light light = new Light();
                light.setLabel(context.label());
                light.setColor(randomColorEntityFromUserRequest);
                light.setEnabled(false);

                entityManager.persist(light);
            }

            // Get Entity from Lights from DB for label
            TypedQuery<Light> queryEntityFromLightsDBForLabel = entityManager.createQuery(
                    "select l from Light l where l.label = ?1", Light.class);
            queryEntityFromLightsDBForLabel.setParameter(1, context.label());
            List<Light> entityFromLightsDBForLabel = queryEntityFromLightsDBForLabel.getResultList();
            Light light = entityFromLightsDBForLabel.get(0);

            // Check enabled
            if (light.isEnabled()) {
                throw new LightshowException("The light exists, but it is currently enabled");
            }

            // Light's operation
            light.setEnabled(true);
            long count = context.numberOfSwitching();
            Color oldEntityColor;
            Color newEntityColor;
            do {
                // Change the light's Color
                oldEntityColor = light.getColor();
                do {
                    index = new Random().nextInt(context.colors().size());
                    newEntityColor = listEntityForColorFromUserRequest.get(index);
                } while (oldEntityColor.getName().equals(newEntityColor.getName()));
                light.setColor(newEntityColor);

                light.setEnabled(false);

                Timestamp executionTime = Timestamp.from(LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC));

                // Create ColorHistoryRecord
                ColorHistoryRecord colorHistory = new ColorHistoryRecord();
                colorHistory.setLight(light);
                colorHistory.setOldColor(oldEntityColor);
                colorHistory.setNewColor(light.getColor());
                colorHistory.setChangedAt(executionTime);

                entityManager.persist(colorHistory);

                Thread.sleep(context.switchingInterval() * 1000);

                count--;
            } while (count != 0);

            // Get all ColorHistoryRecord from ColorHistoryDB
            TypedQuery<ColorHistoryRecord> queryEntityFromColorHistoryDB = entityManager.createQuery(
                    "select ch from ColorHistoryRecord ch where ch.light = ?1", ColorHistoryRecord.class);
            queryEntityFromColorHistoryDB.setParameter(1, light);
            List<ColorHistoryRecord> entityFromColorHistoryDB = queryEntityFromColorHistoryDB.getResultList();

            Map<String, List<ColorHistoryRecord>> collectionDTO = new LinkedHashMap<>();
            collectionDTO.put(context.label(), entityFromColorHistoryDB);

            entityManager.getTransaction().commit();

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

    private void checkColorIntervalSwitching(EntityManager entityManager, CreateInputRequest context) throws LightshowException {
        // Check light's label for empty or contain only whitespace code points
        if (context.label().isBlank()) {
            throw new LightshowException(
                    "The light's label must not be empty or contain only whitespace code points'");
        }

        // Check user list for non-existent colors
        TypedQuery<String> queryColorsFromColorsDB = entityManager.createQuery(
                "select c.name from Color c", String.class);
        List<String> colorsFromColorsDB = queryColorsFromColorsDB.getResultList();
        if (!(new HashSet<>(colorsFromColorsDB).containsAll(context.colors()))) {
            throw new LightshowException("There are non-existent colors in the list like " +
                    "'white black brown red orange yellow green blue purple grey'");
        }

        // Check switching interval (must be >= 1)
        if (context.switchingInterval() < 1) {
            throw new LightshowException("The interval is less than 1");
        }

        // Check number of switching (must be >= 1)
        if (context.numberOfSwitching() < 1) {
            throw new LightshowException("The number of switches is less than 1");
        }
    }

    private List<Color> createListEntityForColorFromUserRequest(List<Color> allEntityFromColorsDB) {
        List<Color> listEntityForColorFromUserRequest = new ArrayList<>();
        for (String s : context.colors()) {
            for (Color c : allEntityFromColorsDB) {
                if (s.equals(c.getName())) {
                    listEntityForColorFromUserRequest.add(c);
                }
            }
        }
        return listEntityForColorFromUserRequest;
    }

}
