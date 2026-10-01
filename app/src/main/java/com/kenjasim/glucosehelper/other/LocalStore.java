package com.kenjasim.glucosehelper.other;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Stores all of the app's data on the phone using SharedPreferences.
 * Entries and foods are saved as JSON lists, settings are saved as plain strings.
 * Screens can register a listener to be told when the data changes so they can refresh.
 */
public class LocalStore {

    private static final String PREFS_NAME = "glucose_helper";
    private static final String KEY_ENTRIES = "entries";
    private static final String KEY_FOODS = "foods";
    private static final String KEY_CARB_RATIO = "carb_ratio";
    private static final String KEY_BG_RATIO = "bg_ratio";
    private static final String KEY_IDEAL_LEVEL = "ideal_level";
    private static final String KEY_NAME = "name";

    private static final List<Runnable> listeners = new ArrayList<>();

    private final SharedPreferences prefs;

    public LocalStore(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // ---- Change listeners ----

    public static void addListener(Runnable listener) {
        listeners.add(listener);
    }

    public static void removeListener(Runnable listener) {
        listeners.remove(listener);
    }

    private static void notifyListeners() {
        for (Runnable listener : new ArrayList<>(listeners)) {
            listener.run();
        }
    }

    public static String newId() {
        return UUID.randomUUID().toString();
    }

    // ---- Settings ----

    public String getCarbRatio() {
        return prefs.getString(KEY_CARB_RATIO, "10");
    }

    public String getBgRatio() {
        return prefs.getString(KEY_BG_RATIO, "1");
    }

    public String getIdealLevel() {
        return prefs.getString(KEY_IDEAL_LEVEL, "7");
    }

    public void setRatios(String carbRatio, String bgRatio, String idealLevel) {
        prefs.edit()
                .putString(KEY_CARB_RATIO, carbRatio)
                .putString(KEY_BG_RATIO, bgRatio)
                .putString(KEY_IDEAL_LEVEL, idealLevel)
                .apply();
        notifyListeners();
    }

    public String getName() {
        return prefs.getString(KEY_NAME, null);
    }

    public void setName(String name) {
        prefs.edit().putString(KEY_NAME, name).apply();
        notifyListeners();
    }

    public void clearAll() {
        prefs.edit().clear().apply();
        notifyListeners();
    }

    // ---- Glucose entries ----

    public List<GlucoseData> getEntries() {
        List<GlucoseData> entries = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(prefs.getString(KEY_ENTRIES, "[]"));
            for (int i = 0; i < array.length(); i++) {
                JSONObject o = array.getJSONObject(i);
                entries.add(new GlucoseData(
                        o.optString("bloodLevels"),
                        o.optString("carbohydrates"),
                        o.optString("insulin"),
                        o.optString("dateTime"),
                        o.optString("dataID"),
                        o.optString("timestamp"),
                        o.optString("notes")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return entries;
    }

    /** Adds the entry, or replaces the existing one with the same id. */
    public void saveEntry(GlucoseData entry) {
        List<GlucoseData> entries = getEntries();
        boolean replaced = false;
        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).getDataID().equals(entry.getDataID())) {
                entries.set(i, entry);
                replaced = true;
            }
        }
        if (!replaced) {
            entries.add(entry);
        }
        writeEntries(entries);
    }

    public void deleteEntry(String dataID) {
        List<GlucoseData> entries = getEntries();
        for (int i = entries.size() - 1; i >= 0; i--) {
            if (entries.get(i).getDataID().equals(dataID)) {
                entries.remove(i);
            }
        }
        writeEntries(entries);
    }

    private void writeEntries(List<GlucoseData> entries) {
        JSONArray array = new JSONArray();
        try {
            for (GlucoseData e : entries) {
                JSONObject o = new JSONObject();
                o.put("bloodLevels", e.getBloodLevels());
                o.put("carbohydrates", e.getCarbohydrates());
                o.put("insulin", e.getInsulin());
                o.put("dateTime", e.getDateTime());
                o.put("dataID", e.getDataID());
                o.put("timestamp", e.getTimestamp());
                o.put("notes", e.getNotes());
                array.put(o);
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        prefs.edit().putString(KEY_ENTRIES, array.toString()).apply();
        notifyListeners();
    }

    // ---- Foods ----

    public List<CarbData> getFoods() {
        List<CarbData> foods = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(prefs.getString(KEY_FOODS, "[]"));
            for (int i = 0; i < array.length(); i++) {
                JSONObject o = array.getJSONObject(i);
                foods.add(new CarbData(
                        o.optString("foodName"),
                        o.optString("carbs"),
                        o.optString("amount"),
                        o.optString("dataid")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return foods;
    }

    /** Adds the food, or replaces the existing one with the same id. */
    public void saveFood(CarbData food) {
        List<CarbData> foods = getFoods();
        boolean replaced = false;
        for (int i = 0; i < foods.size(); i++) {
            if (foods.get(i).getDataid().equals(food.getDataid())) {
                foods.set(i, food);
                replaced = true;
            }
        }
        if (!replaced) {
            foods.add(food);
        }
        writeFoods(foods);
    }

    public void deleteFood(String dataid) {
        List<CarbData> foods = getFoods();
        for (int i = foods.size() - 1; i >= 0; i--) {
            if (foods.get(i).getDataid().equals(dataid)) {
                foods.remove(i);
            }
        }
        writeFoods(foods);
    }

    private void writeFoods(List<CarbData> foods) {
        JSONArray array = new JSONArray();
        try {
            for (CarbData f : foods) {
                JSONObject o = new JSONObject();
                o.put("foodName", f.getFoodName());
                o.put("carbs", f.getCarbs());
                o.put("amount", f.getAmount());
                o.put("dataid", f.getDataid());
                array.put(o);
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        prefs.edit().putString(KEY_FOODS, array.toString()).apply();
        notifyListeners();
    }
}
