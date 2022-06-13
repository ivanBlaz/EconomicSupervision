package com.devivan.economicsupervision.System;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.TextureView;
import android.widget.Toast;

import com.devivan.economicsupervision.Objects.Device.Device;
import com.devivan.economicsupervision.Objects.Device.DeviceAccounts.DeviceAccount;
import com.devivan.economicsupervision.R;
import com.devivan.economicsupervision.SQLite.SQLiteConnection;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.internal.$Gson$Types;

import java.io.File;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

class DataRestorationException extends Exception {
    public DataRestorationException() {
        super();
    }
}

public class DataRestoration {
    private System system;

    public void restoreData(System system, boolean startAccount) {
        this.system = system;
        this.system.connect();
        ArrayList<Integer> badPositions = new ArrayList<>();
        if (System.device.accounts != null && System.device.accounts.size() > 0) {
            for (DeviceAccount da : System.device.accounts) {
                ArrayList<String> requests = new ArrayList<>();
                if (da.r != null && da.r.size() > 0) requests.addAll(da.r);
                if (da.auxR != null && da.auxR.size() > 0) requests.addAll(da.auxR);
                if (requests.size() > 0) {
                    deleteAccountIfNecessary(da.getId(), requests.get(0));
                    try {
                        executeRequests(da.getId(), requests);
                    } catch (Exception e) {
                        badPositions.add(da.getPos());
                        da.r = new ArrayList<>();
                        da.auxR = new ArrayList<>();
                    }
                }
            }

            if (system.ghostAccounts != null && system.ghostAccounts.size() > 0) {
                for (DeviceAccount da : system.ghostAccounts) {
                    if (System.device.accounts.stream().noneMatch(a -> a.getId().equals(da.getId()))) {
                        deleteAccount(da.getId());
                    }
                }
            }

            if (badPositions.size() > 0) {
                // Has exceptions
                system.toast(system.activity, System.WARNING_TOAST, system.activity.getString(R.string.Something_went_wrong), Toast.LENGTH_SHORT);

                // Broke ids of accounts
                system.brokeAccountIds();

                // Send request
                FirebaseDatabase.getInstance().getReference("devices/" + system.devId + "/accounts").setValue(System.device.accounts).addOnSuccessListener(aVoid -> {

                    // Build ids of accounts
                    system.buildAccountIds();

                    if (startAccount) {
                        system.dismissDialog();
                        System.waiting = false;
                        system.startAccount();
                    }
                });
            } else if (startAccount) {
                system.dismissDialog();
                System.waiting = false;
                system.startAccount();
            }
        }
    }

    private void deleteAccountIfNecessary(String accountId, String r) {
        if (r.length() > 0 && r.charAt(0) == 'I' && r.split(" ").length == 3 && r.split(" ")[2].split(",").length == 2) {
            if (system.count("accounts", "id = ?", new String[]{accountId}) > 0) deleteAccount(accountId);
        }
    }

    @SuppressLint("SimpleDateFormat")
    private void executeRequests(String accountId, ArrayList<String> requests) throws DataRestorationException {
        for (String r : requests) {
            if (isInsert(r)) {
                // Data
                String obj = getObject(r);
                String[] words = r.split(" ");
                String date = !obj.equals("A") && words.length == 4 ? words[2].replace("_"," ") : null;
                //////////////////////////////////////////////////////////////////////////////////////////////////////////

                if (date != null) try { new SimpleDateFormat(System.datePattern).parse(date); } catch (ParseException ignored) { throw new DataRestorationException(); }

                // Account
                if (obj.equals("A")) {
                    String[] strings = words[2].split(",");
                     if (words.length == 3 && strings.length == 2 && strings[0].chars().allMatch(Character::isUpperCase) && system.doesThisCurrencyExist(strings[0])) {
                        double value; try { value = Double.parseDouble(strings[1]); } catch (NumberFormatException ignored) { throw new DataRestorationException(); }
                        if (Math.abs(value) == 0 || (Math.abs(value) >= System.MIN_VALUE && Math.abs(value) <= System.MAX_VALUE)) { insertAccount(accountId, strings[0], value); }
                        else throw new DataRestorationException();
                    } else throw new DataRestorationException();
                }
                // Should / Have
                else if (obj.equals("SH") || obj.equals("HA")) {
                    if (date != null) {
                        String[] strings = words[3].split(",");
                        if (strings.length == 2) {
                            if (strings[0].chars().filter(Character::isLetter).allMatch(Character::isLowerCase) && strings[0].chars().anyMatch(Character::isDigit)) {
                                double value; try { value = Double.parseDouble(strings[1]); } catch (NumberFormatException ignored) { throw new DataRestorationException(); }
                                if (Math.abs(value) >= System.MIN_VALUE && Math.abs(value) <= System.MAX_VALUE) {
                                    insertShHa(accountId, date, strings[0], obj, value);
                                } else throw new DataRestorationException();
                            } else throw new DataRestorationException();
                        } else throw new DataRestorationException();
                    } else throw new DataRestorationException();
                }
                // Group Payment + Friends
                else if (obj.equals("GP")) {
                    if (date != null) {
                        String[] strings = words[3].split(",");
                        if (strings.length >= 4) {
                            String groupName = strings[0].replace("_"," ");
                            if (groupName.length() > 0) {
                                double value; try { value = Double.parseDouble(strings[1]); } catch (NumberFormatException ignored) { throw new DataRestorationException(); }
                                if (Math.abs(value) >= System.MIN_VALUE && Math.abs(value) <= System.MAX_VALUE) {
                                    ArrayList<String> lookUpKeys = new ArrayList<>(Arrays.asList(strings).subList(2, strings.length));
                                    if (lookUpKeys.stream().allMatch(l -> l.chars().filter(Character::isLetter).allMatch(Character::isLowerCase) && l.chars().anyMatch(Character::isDigit))) {
                                        insertGroupPayment(accountId, date, groupName.replace("_"," "), value, lookUpKeys);
                                    } else throw new DataRestorationException();
                                } else throw new DataRestorationException();
                            } else throw new DataRestorationException();
                        } else throw new DataRestorationException();
                    } else throw new DataRestorationException();
                }
                // Invert / Evert + Concept
                else if (obj.equals("I") || obj.equals("E")) {
                    if (date != null) {
                        String[] strings = words[3].split(",");
                        if (strings.length == 2 || strings.length == 3) {
                            String location = null;
                            ArrayList<String> stringArray = new ArrayList<>(Arrays.asList(strings));
                            if (stringArray.size() == 3) {
                                if (stringArray.get(0).chars().filter(c -> c == '·').count() == 4) {
                                    location = stringArray.get(0);
                                    stringArray.remove(0);
                                } else throw new DataRestorationException();
                            }

                            if (stringArray.get(0).length() > 0) {
                                double value;
                                try { value = Double.parseDouble(stringArray.get(1)); } catch (NumberFormatException ignored) { throw new DataRestorationException(); }
                                if (Math.abs(value) >= System.MIN_VALUE && Math.abs(value) <= System.MAX_VALUE) {
                                    int conceptId;
                                    if (stringArray.get(0).startsWith(":")) {
                                        insertConcept(accountId, stringArray.get(0).substring(1).replace("_"," "));
                                        @SuppressLint("Recycle") Cursor c = system.read().rawQuery("select max(id) from concepts where accountId = ?", new String[]{accountId});
                                        if (c.moveToFirst()) { conceptId = c.getInt(0); } else throw new DataRestorationException();
                                    } else try { conceptId = Integer.parseInt(stringArray.get(0)); } catch (NumberFormatException ignored) { throw new DataRestorationException(); }
                                    if (conceptId != 0) { insertMovement(accountId, date, location != null ? location.replace("_", " ") : null, conceptId, obj, value); } else throw new DataRestorationException();
                                } else throw new DataRestorationException();
                            } else throw new DataRestorationException();
                        } else throw new DataRestorationException();
                    } else throw new DataRestorationException();
                }
                // Expense / Income
                else if (obj.equals("EX") || obj.equals("IN")) {
                    if (date != null) {
                        String[] strings = words[3].split(",");
                        if (strings.length == 2 || strings.length == 3) {
                            String location = null;
                            ArrayList<String> stringArray = new ArrayList<>(Arrays.asList(strings));
                            if (stringArray.size() == 3) {
                                if (stringArray.get(0).chars().filter(c -> c == '·').count() == 4) {
                                    location = stringArray.get(0);
                                    stringArray.remove(0);
                                } else throw new DataRestorationException();
                            }

                            if (stringArray.get(0).length() > 0) {
                                double value; try { value = Double.parseDouble(stringArray.get(1)); } catch (NumberFormatException ignored) { throw new DataRestorationException(); }
                                if (Math.abs(value) >= System.MIN_VALUE && Math.abs(value) <= System.MAX_VALUE) {
                                    if (TextUtils.isDigitsOnly(stringArray.get(0).replace("#",""))) {
                                        insertMovement(accountId, date, location != null ? location.replace("_", " ") : null, stringArray.get(0), obj, value);
                                    } else throw new DataRestorationException();
                                } else throw new DataRestorationException();
                            } else throw new DataRestorationException();
                        } else throw new DataRestorationException();
                    } else throw new DataRestorationException();
                } else throw new DataRestorationException();
            } else if (isUpdate(r)) {
                // Data
                String obj = getObject(r);
                String[] words = r.split(" ");
                String date = !obj.equals("A") && !obj.equals("GP") && words.length == 4 && words[2].contains("_") ? words[2].replace("_"," ") : null;
                /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                if (date != null) try { new SimpleDateFormat(System.datePattern).parse(date); } catch (ParseException ignored) { throw new DataRestorationException(); }

                // Account
                if (obj.equals("A")) {
                    if (words.length == 3) {
                        String[] strings = words[2].split(",");
                        if (strings.length == 1 || strings.length == 2) {
                            if (strings.length == 1) {
                                if (strings[0].length() == 3 && strings[0].chars().allMatch(Character::isUpperCase) && system.doesThisCurrencyExist(strings[0])) {
                                    updateCurrency(accountId, strings[0]);
                                } else {
                                    double value;
                                    try { value = Double.parseDouble(strings[0]); } catch (NumberFormatException ignored) { throw new DataRestorationException(); }
                                    if (Math.abs(value) == 0 || (Math.abs(value) >= System.MIN_VALUE && Math.abs(value) <= System.MAX_VALUE)) { updateMoney(accountId, value, false); }
                                    else throw new DataRestorationException();
                                }
                            } else {
                                if (strings[0].length() == 3 && strings[0].chars().allMatch(Character::isUpperCase) && system.doesThisCurrencyExist(strings[0])) {
                                    double value;
                                    try { value = Double.parseDouble(strings[1]); } catch (NumberFormatException ignored) { throw new DataRestorationException(); }
                                    if (Math.abs(value) >= System.MIN_VALUE && Math.abs(value) <= System.MAX_VALUE) { updateAccount(accountId, strings[0], value); }
                                    else throw new DataRestorationException();
                                } else throw new DataRestorationException();
                            }
                        } else throw new DataRestorationException();
                    } else throw new DataRestorationException();
                }
                // Should / Have [ lookUpKey / transaction ]
                else if (obj.equals("SH") || obj.equals("HA")) {
                    if (words.length == 4) {
                        if (date == null) {
                            ArrayList<String> lookUpKeys = new ArrayList<>(Arrays.asList(words).subList(2, words.length));
                            if (lookUpKeys.size() == 2 && lookUpKeys.stream().allMatch(l -> l.chars().filter(Character::isLetter).allMatch(Character::isLowerCase) && l.chars().anyMatch(Character::isDigit))) {
                                updateLookUpKey(accountId, obj, lookUpKeys.get(0), lookUpKeys.get(1));
                            } else throw new DataRestorationException();
                        } else {
                            if (TextUtils.isDigitsOnly(words[3])) {
                                try { int id = Integer.parseInt(words[3]); updateNewTransaction(accountId, date, id); } catch (NumberFormatException ignored) { throw new DataRestorationException(); }
                            } else if (words[3].chars().filter(Character::isLetter).allMatch(Character::isLowerCase) && words[3].chars().anyMatch(Character::isDigit)) {
                                updateNewTransactions(accountId, date, words[3]);
                            } else throw new DataRestorationException();
                        }
                    } else throw new DataRestorationException();
                }
                // GP [ name ]
                else if (obj.equals("GP")) {
                    if (words.length == 4) {
                        if (TextUtils.isDigitsOnly(words[2]) && words[3].length() > 0) {
                            try { int id = Integer.parseInt(words[2]); updateGroupName(words[3].replace("_"," "), id); } catch (NumberFormatException ignored) { throw new DataRestorationException(); }
                        } else throw new DataRestorationException();
                    } else throw new DataRestorationException();
                } else throw new DataRestorationException();
            } else if (isDelete(r)) {
                // Data
                String obj = getObject(r);
                String[] words = r.split(" ");
                /////////////////////////////////////

                // Account
                if (obj.equals("A")) {
                    if (words.length == 1) { deleteAccount(accountId);} else throw new DataRestorationException();
                }
                // Should / Have [ one / all ]
                else if (obj.equals("SH") || obj.equals("HA")) {
                    if (words.length == 3) {
                        if (TextUtils.isDigitsOnly(words[2])) {
                            try { int id = Integer.parseInt(words[2]); deletePayment(id);} catch (NumberFormatException ignored) { throw new DataRestorationException(); }
                        } else if (words[2].chars().filter(Character::isLetter).allMatch(Character::isLowerCase) && words[2].chars().anyMatch(Character::isDigit)) {
                            deletePayments(accountId, obj, words[2]);
                        } else throw new DataRestorationException();
                    } else throw new DataRestorationException();
                }
                // GP
                else if (obj.equals("GP")) {
                    if (words.length == 3) {
                        if (TextUtils.isDigitsOnly(words[2])) {
                            try { int id = Integer.parseInt(words[2]); deleteGroup(accountId, id); } catch (NumberFormatException ignored) { throw new DataRestorationException(); }
                        } else throw new DataRestorationException();
                    } else throw new DataRestorationException();
                }
                // Concept
                else if (obj.equals("C")) {
                    if (words.length == 3) {
                        if (TextUtils.isDigitsOnly(words[2])) {
                            try { int id = Integer.parseInt(words[2]); deleteConcept(accountId, id); } catch (NumberFormatException ignored) { throw new DataRestorationException(); }
                        } else throw new DataRestorationException();
                    } else throw new DataRestorationException();
                }
                // Movement
                else if (obj.equals("M")) {
                    if (words.length == 3) {
                        if (TextUtils.isDigitsOnly(words[2])) {
                            try { int id = Integer.parseInt(words[2]); deleteMovement(accountId, id); } catch (NumberFormatException ignored) { throw new DataRestorationException(); }
                        } else throw new DataRestorationException();
                    } else throw new DataRestorationException();
                }
                // Transaction [ one / all ]
                else if (obj.equals("TR")) {
                    if (words.length == 3) {
                        if (TextUtils.isDigitsOnly(words[2])) {
                            try { int id = Integer.parseInt(words[2]); deleteTransaction(accountId, id); } catch (NumberFormatException ignored) { throw new DataRestorationException(); }
                        } else if (words[2].chars().filter(Character::isLetter).allMatch(Character::isLowerCase) && words[2].chars().anyMatch(Character::isDigit)) {
                            deleteTransactions(accountId, words[2]);
                        } else throw new DataRestorationException();
                    } else throw new DataRestorationException();
                } else throw new DataRestorationException();
            } else throw new DataRestorationException();
        }
    }

    private boolean isInsert(String r) {
        return r.length() > 0 && r.charAt(0) == 'I' && (r.split(" ").length == 3 || r.split(" ").length == 4);
    }

    private boolean isUpdate(String r) {
        return r.length() > 0 && r.charAt(0) == 'U' && (r.split(" ").length == 3 || r.split(" ").length == 4);
    }

    private boolean isDelete(String r) {
        return r.length() > 0 && r.charAt(0) == 'D' && (r.split(" ").length == 1 || r.split(" ").length == 3);
    }

    private String getObject(String r) {
        return r.length() == 1 ? "A" : r.split(" ")[1];
    }

    ////////////////
    // Insertions //
    ////////////////
    // Account
    private void insertAccount(String accountId, String currency, double money) {
        system.write().execSQL("insert into accounts (id,currency,money) " +
                        "select ?,?,? " +
                        "where not exists(select id from accounts where id = '" + accountId + "')",
                new String[]{accountId,currency,String.valueOf(money)});
    }

    // SH / HA
    private void insertShHa(String accountId, String date, String lookUpKey, String type, double value) {
        system.write().execSQL("insert into movements (accountId,date,location,type,value) " +
                        "values(?,?,?,?,?)",
                new String[]{accountId,date,lookUpKey,type,String.valueOf(value)});
    }

    // GP
    private void insertGroupPayment(String accountId, String date, String groupName, double value, ArrayList<String> lookUpKeys) {
        system.write().execSQL("insert into movements (accountId,date,location,type,value) " +
                "values(?,?,?,?,?)",
                new String[]{accountId,date,groupName,"GP",String.valueOf(value)});
        @SuppressLint("Recycle") Cursor c = system.read().rawQuery("select max(id) from movements where accountId = ?", new String[]{accountId});
        if (c.moveToFirst()) {
            int id = c.getInt(0);
            for (String lookUpKey : lookUpKeys) {
                system.write().execSQL("insert into movements (accountId,conceptId,date,location,type,value) " +
                                "values(?,?,?,?,?,?)",
                        new String[]{accountId,String.valueOf(id),date,lookUpKey,"HA",String.valueOf(value)});
            }
        }
    }

    // Concept
    private void insertConcept(String accountId, String conceptName) {
        system.write().execSQL("insert into concepts (accountId,name) " +
                        "values(?,?)",
                new String[]{accountId,conceptName});
    }

    // Movement [ Concept ]
    private void insertMovement(String accountId, String date, String location, int conceptId, String type, double value) {
        system.write().execSQL("insert into movements (accountId,conceptId,date,location,type,value) " +
                        "values(?,?,?," + (location != null ? "'" + location + "'" : "null") + ",?,?)",
                new String[]{accountId,String.valueOf(conceptId),date,type,String.valueOf(value)});
        updateMoney(accountId, value, true);
    }

    // Movement [ Category ]
    private void insertMovement(String accountId, String date, String location, String subCategoryId, String type, double value) {
        system.write().execSQL("insert into movements (accountId,subCategoryId,date,location,type,value) " +
                        "values(?,?,?," + (location != null ? "'" + location + "'" : "null") + ",?,?)",
                new String[]{accountId,subCategoryId,date,type,String.valueOf(value)});
        updateMoney(accountId, value, true);
    }

    /////////////
    // Updates //
    /////////////
    // Currency
    private void updateCurrency(String accountId, String currency) {
        system.write().execSQL("update accounts set currency = ? where id = ?", new String[]{currency, accountId});
    }

    // Money
    private void updateMoney(String accountId, double value, boolean increment) {
        system.write().execSQL("update accounts set money = " + (increment ? "money +" : "") + " ? where id = ?", new String[]{String.valueOf(value), accountId});
    }

    // Account
    private void updateAccount(String accountId, String currency, double money) {
        system.write().execSQL("update accounts set currency = ?, money = ? where id = ?", new String[]{currency, String.valueOf(money), accountId});
    }

    // SH / HA [ lookUpKey ]
    private void updateLookUpKey(String accountId, String type, String oldLookUpKey, String newLookUpKey) {
        system.write().execSQL("update movements set location = ? where location = ? and type = ? and accountId = ?", new String[]{newLookUpKey, oldLookUpKey, type, accountId});
    }

    // TR [ one ]
    private void updateNewTransaction(String accountId, String date, int id) {
        @SuppressLint("Recycle") Cursor c = system.read().rawQuery("select value from movements where id = ?", new String[]{String.valueOf(id)});
        if (c.moveToFirst()) {
            double value = c.getDouble(0);
            system.write().execSQL("update movements set date = ?, type = 'TR' where id = ?", new String[]{date, String.valueOf(id)});
            updateMoney(accountId, value, true);
        }
    }

    // TR [ all ]
    private void updateNewTransactions(String accountId, String date, String lookUpKey) {
        @SuppressLint("Recycle") Cursor c = system.read().rawQuery("select sum(value) from movements where location = ? and accountId = ?", new String[]{lookUpKey, accountId});
        if (c.moveToFirst()) {
            double value = c.getDouble(0);
            system.write().execSQL("update movements set date = ?, type = 'TR' where location = ? and accountId = ?", new String[]{date, lookUpKey, accountId});
            updateMoney(accountId, value, true);
        }
    }

    // GP [ name ]
    private void updateGroupName(String name, int id) {
        system.write().execSQL("update movements set location = ? where id = ?", new String[]{name, String.valueOf(id)});
    }


    /////////////
    // Deletes //
    /////////////
    // Account
    private void deleteAccount(String accountId) {
        system.write().execSQL("delete from movements where accountId = ?", new String[]{accountId});
        system.write().execSQL("delete from concepts where accountId = ?", new String[]{accountId});
        system.write().execSQL("delete from accounts where id = ?", new String[]{accountId});
    }

    // Should / Have [ one ]
    private void deletePayment(int id) {
        system.write().execSQL("delete from movements where id = ?", new String[]{String.valueOf(id)});
    }

    // Should / Have [ all ]
    private void deletePayments(String accountId, String type, String lookUpKey) {
        system.write().execSQL("delete from movements where type = ? and location = ? and accountId = ?", new String[]{type, lookUpKey, accountId});
    }

    // GP
    private void deleteGroup(String accountId, int id) {
        @SuppressLint("Recycle") Cursor c = system.read().rawQuery("select value," +
                        "(select sum(value) from movements where conceptId = m.id and type = 'TR')" +
                        "from movements m where id = ?",
                new String[]{String.valueOf(id)});
        if (c.moveToFirst()) {
            double value = -(c.isNull(1) ? 0 : c.getDouble(1));
            system.write().execSQL("delete from movements where conceptId = ? and type in('HA','TR') and accountId = ?", new String[]{String.valueOf(id), accountId});
            system.write().execSQL("delete from movements where id = ?", new String[]{String.valueOf(id)});
            updateMoney(accountId, value, true);
        }
    }

    // Concept
    private void deleteConcept(String accountId, int id) {
        system.write().execSQL("delete from movements where conceptId = ? and type in('E','I') and accountId = ?",
                new String[]{String.valueOf(id), accountId});
        system.write().execSQL("delete from concepts where id = ?", new String[]{String.valueOf(id)});
    }

    // Movement
    private void deleteMovement(String accountId, int id) {
        @SuppressLint("Recycle") Cursor c = system.read().rawQuery("select value from movements where id = ?", new String[]{String.valueOf(id)});
        if (c.moveToFirst()) {
            double value = -c.getDouble(0);
            system.write().execSQL("delete from movements where id = ?", new String[]{String.valueOf(id)});
            updateMoney(accountId, value, true);
        }
    }

    // TR [ one ]
    private void deleteTransaction(String accountId, int id) {
        @SuppressLint("Recycle") Cursor c = system.read().rawQuery("select value from movements where id = ?", new String[]{String.valueOf(id)});
        if (c.moveToFirst()) {
            double value = -c.getDouble(0);
            system.write().execSQL("delete from movements where id = ?", new String[]{String.valueOf(id)});
            updateMoney(accountId, value, true);
        }
    }

    // TR [ all ]
    private void deleteTransactions(String accountId, String lookUpKey) {
        @SuppressLint("Recycle") Cursor c = system.read().rawQuery("select sum(value) from movements where type = 'TR' and location = ? and accountId = ?", new String[]{lookUpKey, accountId});
        if (c.moveToFirst()) {
            double value = -c.getDouble(0);
            system.write().execSQL("delete from movements where type = 'TR' and location = ? and accountId = ?", new String[]{lookUpKey, accountId});
            updateMoney(accountId, value, true);
        }
    }
}
