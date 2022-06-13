package com.devivan.economicsupervision.System;

import android.annotation.SuppressLint;
import android.content.ContentProviderClient;
import android.database.Cursor;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devivan.economicsupervision.Activities.MovementsActivity;
import com.devivan.economicsupervision.Activities.TransactionsActivity;
import com.devivan.economicsupervision.Adapters.MovementsAdapter.MovementsAdapter;
import com.devivan.economicsupervision.Adapters.TransactionsAdapter.TransactionsAdapter;
import com.devivan.economicsupervision.Fragments.ShouldFragment;
import com.devivan.economicsupervision.Fragments.TransactionsFragment;
import com.devivan.economicsupervision.Objects.Account.Categories.Category;
import com.devivan.economicsupervision.Objects.Account.Categories.SubCategories.SubCategory;
import com.devivan.economicsupervision.Objects.Account.Friend.Friend;
import com.devivan.economicsupervision.Objects.Account.Group.Group;
import com.devivan.economicsupervision.Objects.Account.Movements.Movement;
import com.devivan.economicsupervision.Objects.Account.Payment.Payment;
import com.devivan.economicsupervision.R;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

public class VoiceAssistant {
    // Activity + AccountId + Sentence
    @SuppressLint("StaticFieldLeak")
    private static System system;
    private static RecyclerView recyclerView;
    public static TransactionsFragment fragment;
    private static String sentence;
    ///////////////////////////////

    // View Types
    static final int GROUP_VIEW_TYPE = 0;
    static final int FRIEND_VIEW_TYPE = 1;
    static final int PAYMENT_VIEW_TYPE = 2;
    static final int MOVEMENT_VIEW_TYPE = 3;
    static final int DATE_VIEW_TYPE = 4;
    ////////////////////////////////////

    public static int getViewType(Object o) {
        if (o instanceof Group) return GROUP_VIEW_TYPE;
        else if (o instanceof Friend) return FRIEND_VIEW_TYPE;
        else if (o instanceof Payment) return PAYMENT_VIEW_TYPE;
        else if (o instanceof Movement) return MOVEMENT_VIEW_TYPE;
        else return DATE_VIEW_TYPE;
    }

    public static boolean isGroup(int i) {
        return i == GROUP_VIEW_TYPE;
    }

    public static boolean isFriend(int i) {
        return i == FRIEND_VIEW_TYPE;
    }

    public static boolean isPayment(int i) {
        return i == PAYMENT_VIEW_TYPE;
    }

    public static boolean isMovement(int i) {
        return i == MOVEMENT_VIEW_TYPE;
    }

    public static boolean isDate(int i) {
        return i == DATE_VIEW_TYPE;
    }

    // Filter data [ sentence ]
    public static void filterData(System system, RecyclerView recyclerView, TransactionsFragment fragment, String sentence) {
        // Connect to SQLite
        system.connect();

        // Set data
        VoiceAssistant.system = system;
        VoiceAssistant.recyclerView = recyclerView;
        VoiceAssistant.fragment = fragment;
        ///////////////////////////////////


        if (system.activity instanceof TransactionsActivity) {
            int x = tSentenceTranslation(sentence);
            if (sentence == null || x == -1 || x == 2) {
                // Reset data
                offset = 0;
                tSentence = x;
                tGroupBy = x == -1 ? 2 : 0;
                if (tGroupBy == 2) VoiceAssistant.sentence = prepareSentence(sentence);
            } else tSentence = x;
            if (recyclerView.getAdapter() == null) setAdapter();
            else refreshAdapter(recyclerView.getAdapter(), getTransactions());
        } else if (system.activity instanceof MovementsActivity) {
            offset = 0;
            VoiceAssistant.sentence = prepareSentence(sentence);
            if (recyclerView.getAdapter() == null) setAdapter();
            else {
                ((MovementsAdapter)recyclerView.getAdapter()).objects = new ArrayList<>();
                refreshAdapter(recyclerView.getAdapter(), getMovements());
            }
        }
    }

    public static void displayFlowOfMoney(TextView textViewCount, TextView textViewValue, boolean in) {
        // Initialize current month
        @SuppressLint("SimpleDateFormat") String month = new SimpleDateFormat("MM").format(new Date());

        // Get the sum of expenses for this month
        @SuppressLint("Recycle") Cursor c = system.read().rawQuery("select count(id), sum(value) from movements where accountId = ? and " +
                (in ? "value > 0" : "value < 0") + " and type = 'TR' and " +
                "date like '%-" + month + "-%'", new String[]{system.def});
        if (c.moveToFirst()) {
            textViewCount.setText(String.valueOf(c.getInt(0)));
            textViewValue.setText(System.moneyFormat.format(c.getDouble(1)));
        } else {
            textViewCount.setText(String.valueOf(0));
            textViewValue.setText(System.moneyFormat.format(0));
        }
    }

    private static String prepareSentence(String sentence) {
        if (sentence == null) return null;
        String s = " " + removeAccents(sentence) + " ";
        String[] numbers = system.activity.getResources().getStringArray(R.array.numbers);
        for (int i = 0; i < numbers.length; i++) s = s.replace(" " + numbers[i].toLowerCase() + " ", " " + i + " ");
        return s.trim();
    }

    // Filter data [ next ]
    public static void nextData() {
        if (system.activity instanceof TransactionsActivity) {
            refreshNewAdapterData(Objects.requireNonNull(recyclerView.getAdapter()), getTransactions());
        } else if (system.activity instanceof MovementsActivity) {
            refreshNewAdapterData(Objects.requireNonNull(recyclerView.getAdapter()), getMovements());
        }
    }

    // Set adapter
    private static void setAdapter() {
        recyclerView.setHasFixedSize(false);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext(), RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(system.activity instanceof TransactionsActivity ? getNewTAdapter() : getNewMAdapter());
        display();
    }

    // Refresh adapter data
    private static void refreshAdapter(RecyclerView.Adapter adapter, ArrayList<Object> newObjects) {
        // Get objects
        ArrayList<Object> objects = adapter instanceof TransactionsAdapter ? ((TransactionsAdapter)adapter).objects : ((MovementsAdapter)adapter).objects;

        // Scroll to first position?
        if (newObjects.size() > 0) recyclerView.scrollToPosition(0);

        // Refresh objects data diff
        objects.clear();
        objects.addAll(newObjects);

        // Notify data set changed
        adapter.notifyDataSetChanged();

        // Display
        display();
    }

    // Refresh adapter new data
    private static void refreshNewAdapterData(RecyclerView.Adapter adapter, ArrayList<Object> newObjects) {
        // Get objects
        ArrayList<Object> objects = adapter instanceof TransactionsAdapter ? ((TransactionsAdapter)adapter).objects : ((MovementsAdapter)adapter).objects;

        // Add new objects
        objects.addAll(newObjects);

        // Notify item range inserted
        adapter.notifyItemRangeInserted(objects.size() - newObjects.size(), newObjects.size());

        // Display
        display();
    }

    public static void display() {
        if (system.activity instanceof TransactionsActivity) {
            if (((TransactionsAdapter)recyclerView.getAdapter()).objects.size() > 0) displayRecyclerView();
            else displayNoneObjectsLayout();
        } else if (system.activity instanceof MovementsActivity) {
            if (((MovementsAdapter)recyclerView.getAdapter()).objects.size() > 0) displayRecyclerView();
            else displayNoneObjectsLayout();
        }
    }

    private static void displayNoneObjectsLayout() {
        if (system.activity instanceof TransactionsActivity) {
            // Change visibility
            fragment.v.findViewById(R.id.clNoneTransaction).setVisibility(View.VISIBLE);
            fragment.v.findViewById(R.id.rvTransactions).setVisibility(View.GONE);

            // Set text
            ((TextView) fragment.v.findViewById(R.id.txtvNoneTransaction)).setText(system.activity.getString(R.string.No_transaction_found));

            // Animations?
            if (!TransactionsActivity.animations.get(1)) {
                TransactionsActivity.animations.put(1, true);
                Animation slideInLeft = AnimationUtils.loadAnimation(system.activity, R.anim.slide_in_left);
                Animation slideInRight = AnimationUtils.loadAnimation(system.activity, R.anim.slide_in_right);
                fragment.v.findViewById(R.id.txtvNoneTransaction).setAnimation(slideInLeft);
                fragment.v.findViewById(R.id.imgvNoneTransaction).setAnimation(slideInRight);
                slideInLeft.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        Animation rotate = AnimationUtils.loadAnimation(system.activity, R.anim.rotate);
                        fragment.v.findViewById(R.id.imgvNoneTransaction).setVisibility(View.VISIBLE);
                        fragment.v.findViewById(R.id.imgvNoneTransaction).setAnimation(rotate);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }

            // Get layout + click listener
            ConstraintLayout layout = fragment.v.findViewById(R.id.clNoneTransaction);
            layout.setOnClickListener(v -> fragment.v.findViewById(R.id.imgvNoneTransaction).startAnimation(AnimationUtils.loadAnimation(system.activity, R.anim.rotate)));
        } else if (system.activity instanceof MovementsActivity) {
            // Get activity
            MovementsActivity activity = ((MovementsActivity) system.activity);

            // Change visibility
            activity.findViewById(R.id.clNoneMovement).setVisibility(View.VISIBLE);
            activity.findViewById(R.id.rvMovements).setVisibility(View.GONE);

            // Set text
            ((TextView) activity.findViewById(R.id.txtvNoneMovement)).setText(system.activity.getString(R.string.No_movement_found));

            // Animations?
            if (!MovementsActivity.animations.get(0)) {
                MovementsActivity.animations.put(0, true);
                Animation slideInLeft = AnimationUtils.loadAnimation(system.activity, R.anim.slide_in_left);
                Animation slideInRight = AnimationUtils.loadAnimation(system.activity, R.anim.slide_in_right);
                activity.findViewById(R.id.txtvNoneMovement).setAnimation(slideInLeft);
                activity.findViewById(R.id.imgvNoneMovement).setAnimation(slideInRight);

                // Set image
                MovementsActivity.animations.put(1, new Random().nextInt((2 - 1) + 1) + 1 == 1);
                ((ImageView)activity.findViewById(R.id.imgvNoneMovement)).setImageResource(MovementsActivity.animations.get(1) ? R.drawable.income : R.drawable.expense);
            }

            // Get layout + click listener
            ConstraintLayout layout = activity.findViewById(R.id.clNoneMovement);
            layout.setOnClickListener(v -> {
                // Change bool + get image
                MovementsActivity.animations.put(1, !MovementsActivity.animations.get(1));
                ImageView imageView = ((ImageView)activity.findViewById(R.id.imgvNoneMovement));

                // Animations
                Animation fadeIn = AnimationUtils.loadAnimation(system.activity, R.anim.fade_in);
                Animation fadeOut = AnimationUtils.loadAnimation(system.activity, R.anim.fade_out);

                // Set animation
                imageView.startAnimation(fadeOut);
                fadeOut.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        ((ImageView)activity.findViewById(R.id.imgvNoneMovement)).setImageResource(MovementsActivity.animations.get(1) ? R.drawable.income : R.drawable.expense);
                        imageView.startAnimation(fadeIn);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            });
        }
    }

    private static void displayRecyclerView() {
        if (system.activity instanceof TransactionsActivity) {
            // Change visibility
            fragment.v.findViewById(R.id.clNoneTransaction).setVisibility(View.GONE);
            fragment.v.findViewById(R.id.rvTransactions).setVisibility(View.VISIBLE);
        } else if (system.activity instanceof MovementsActivity) {
            // Change visibility
            system.activity.findViewById(R.id.clNoneMovement).setVisibility(View.GONE);
            system.activity.findViewById(R.id.rvMovements).setVisibility(View.VISIBLE);
        }
    }

    ///////////////
    // Selection //
    ///////////////
    private static String getType() {
        if (system.activity instanceof TransactionsActivity) {
            if (contains("group transactions","de grupos","grupales")) {
                return "conceptId is not null";
            }
            else if (contains("individual transactions","friend transactions","de amigos","individuales")) {
                return "conceptId is null";
            }
        } else {
            if (contains("category movements","de categoria","de categorias","categorial","categoriales","categoricos")) {
                return "type in('EX','IN')";
            }
            else if (contains("conceptual movements","de concepto","de conceptos","conceptual","conceptuales")) {
                return "type in('I','E')";
            }
        }
        return null;
    }

    private static String getValueType() {
        if (contains("positive", "positivo", "positiva")) return "value > 0";
        else if (contains("negative", "negativo", "negativa")) return "value < 0";
        else return null;
    }

    private static String getUpperLowerBetweenValueOf() {
        String[] more = {"more ","higher than ",
                "superior a ","superior que ","superiores a ","superiores que ",
                "mayor a ","mayor que ","mayores a ","mayores que "};
        String[] less = {"less ","lower than ",
                "inferior a ","inferior que ","inferiores a ","inferiores que ",
                "menor a ","menor que ","menores a ","menores que "};
        String[] between = {"between ","entre "};
        String[] and = {" and "," y "};
        if (contains(more)) {
            double value = getNextValueOfWords(replace(sentence, more, "more "), "more");
            if (value > 0) return "abs(value) > " + value;
        } else if (contains(less)) {
            double value = getNextValueOfWords(replace(sentence, less, "less "), "less");
            if (value > 0) return "abs(value) < " + value;
        } else if (contains(between)) {
            double valueFrom = getNextValueOfWords(replace(sentence, between, "between "), "between");
            if (valueFrom > 0) {
                double valueTo = getNextValueOfWords(replace(sentence, and," and "), "and");
                if (valueTo > 0) return betweenValues(valueFrom, valueTo);
            }
        }
        return null;
    }

    private static String getAfterPriorBetweenAgoDate() {
        String[] after = {" after ", " later ", " posterior ", " posteriores ", " despues "};
        String[] prior = {" prior ", " previous ", " anterior ", " anteriores "};
        String[] moreAgo = {" +ago ", " more than ", " hace mas "};
        String[] lessAgo = {" -ago ", " less than ", " hace menos "};
        String[] ago = {" ago ", " hace "};
        String[] between = {" between ", "entre "};
        String[] exactly = {" exactly ", " exactamente", " exacto "};
        String[] and = {" and ", " y "};

        String s = " " + sentence.toLowerCase() + " ";
        switch (Locale.getDefault().getISO3Language().toLowerCase()) {
            case "eng":
                s = s.replace("a", "1");
                break;
            case "spa":
                s = s.replace(" un ", " 1 ")
                        .replace(" una ", " 1 ");
                break;
        }

        String[] beforeYesterday = {" before_yesterday "," before yesterday "," the day before yesterday "," antes de ayer "," anteayer "};
        String[] yesterday = {" yesterday "," ayer "};
        String[] today = {" today "," hoy "};
        String[] lastWeek = {" last_week ", " the last week ", " last week ", " the past week ", " past week ", " semana pasada ", " pasada semana "};
        String[] theWeek = {" the_week ", " of the week ", " the week ", " this week ", " de la semana ", " la semana ", " esta semana "};
        String[] lastFortnight = {" last_fortnight ", " the last fortnight ", " last fortnight ", " the past fortnight ", " past fortnight ", " quincena pasada ", " pasada quincena "};
        String[] theFortnight = {" the_fortnight ", " of the fortnight ", " the fortnight ", " this fortnight ", " de la quincena ", " la quincena ", " esta quincena "};
        String[] lastMonth = {" last_month ", " the last month ", " last month ", " the past month ", " past month ", " mes pasado ", " pasado mes "};
        String[] theMonth = {" the_month ", " of the month ", " the month ", " this month ", " del mes ", " el mes ", " este mes "};
        String[] lastSeason = {" last_season ", " the last season ", " last season ", " the past season ", " past season ", " estacion pasada ", " pasada estacion "};
        String[] theSeason = {" the_season ", " of the season ", " the season ", " this season ", " de la estacion ", " la estacion ", " esta estacion "};
        String[] lastYear = {" last_year ", " the last year ", " last year ", " the past year ", " past year ", " año pasado ", " pasado año "};
        String[] theYear = {" the_year ", " of the year ", " the year ", " this year ", " del año ", " el año ", " este año "};

        s = replace(s, beforeYesterday, beforeYesterday[0]);
        s = replace(s, yesterday, yesterday[0]);
        s = replace(s, today, today[0]);
        s = replace(s, lastWeek, lastWeek[0]);
        s = replace(s, theWeek, theWeek[0]);
        s = replace(s, lastFortnight, lastFortnight[0]);
        s = replace(s, theFortnight, theFortnight[0]);
        s = replace(s, lastMonth, lastMonth[0]);
        s = replace(s, theMonth, theMonth[0]);
        s = replace(s, lastSeason, lastSeason[0]);
        s = replace(s, theSeason, theSeason[0]);
        s = replace(s, lastYear, lastYear[0]);
        s = replace(s, theYear, theYear[0]);

        // After
        if (contains(after)) {
            s = replace(s, after, after[0]).trim();
            ArrayList<String> words = reformDateTypes(getWords(s));
            if (words != null) {
                String date = getDate(words, false);
                if (date != null) return afterToDate(date);
            }
        }
        // Prior
        else if (contains(prior)) {
            s = replace(s, prior, prior[0]).trim();
            ArrayList<String> words = reformDateTypes(getWords(s));
            if (words != null) {
                String date = getDate(words, false);
                if (date != null) return priorToDate(date);
            }
        }
        // Between
        else if (contains(between)) {
            s = replace(s, ago, ago[0]).replace(" y ", " and ").trim();
            ArrayList<String> words = reformDateTypes(getWords(s));
            if (words != null && words.contains("and")) {
                ArrayList<String> firstWords = getSubWords(words, 0, words.indexOf("and"));
                ArrayList<String> secondWords = getSubWords(words, words.indexOf("and"));
                //
                String dateFrom = firstWords.contains("ago") ? getDateAgo(firstWords) : getDate(firstWords, false);
                String dateTo = secondWords.contains("ago") ? getDateAgo(secondWords) : getDate(secondWords, false);
                if (dateFrom != null && dateTo != null) return betweenDates(dateFrom, dateTo);
            }
        }
        // More than x time ago
        else if (contains(moreAgo)) {
            s = replace(s, moreAgo," +ago ").trim();
            ArrayList<String> words = reformDateTypes(getSubWords(getWords(s), getWords(s).indexOf("+ago")));
            if (words != null) {
                String dateAgo = getDateAgo(words);
                if (dateAgo != null) return "date(date) < date('" + dateAgo + "')";
            }
        }
        // Less than x time ago
        else if (contains(lessAgo)) {
            s = replace(s, lessAgo,lessAgo[0]).trim();
            ArrayList<String> words = reformDateTypes(getSubWords(getWords(s), getWords(s).indexOf("-ago")));
            if (words != null) {
                String dateAgo = getDateAgo(words);
                if (dateAgo != null) return "date(date) > date('" + dateAgo + "')";
            }
        }
        // x time ago
        else if (contains(ago)) {
            s = replace(s, ago,ago[0]);
            s = replace(s, exactly, exactly[0]).trim();
            ArrayList<String> words = reformDateTypes(getSubWords(getWords(s), getWords(s).indexOf("ago")));
            if (words != null) {
                return getDateAgo(words, equals("exactly", false, words));
            }
        }
        // Exact date or during x period
        else {
            ArrayList<String> words = reformDateTypes(getWords(s));
            if (words != null) return getDate(words, true);
        }
        return null;
    }

    private static String getOfCategories() {
        ArrayList<Category> categories = getCategories();

        String separator = " or ";
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(");
        for (int i = 0; i < categories.size(); i++) {
            for (int j = 0; j < categories.get(i).getSubCategories().size(); j++) {
                if (contains(removeAccents(categories.get(i).getSubCategories().get(j).getName()).toLowerCase()))
                    stringBuilder.append("subCategoryId = '").append(i).append("#").append(j).append("'").append(separator);
            }
            if (contains(removeAccents(categories.get(i).getName()).toLowerCase()))
                stringBuilder.append("subCategoryId like '").append(i).append("%'").append(separator);
        }
        if (stringBuilder.length() > 1) stringBuilder = new StringBuilder(stringBuilder.substring(0, stringBuilder.length() - separator.length()));
        stringBuilder.append(")");
        if (stringBuilder.length() > 2) return stringBuilder.toString();
        else return null;
    }

    private static ArrayList<Category> getCategories() {
        ArrayList<Category> categories = new ArrayList<>();
        String[] cats = system.activity.getResources().getStringArray(R.array.categories);

        for (String cat : cats) {
            Category c = new Category(cat.split("\\|")[0]);
            c.subCategories = new ArrayList<>();
            if (cat.contains("|")) {
                String[] subCats = cat.split("\\|")[1].split(",");
                for (String subCat : subCats) c.subCategories.add(new SubCategory(subCat));
            }
            categories.add(c);
        }
        return categories;
    }

    public static String getCategoryName(String subCategoryId) {
        int categoryIndex = Integer.parseInt(subCategoryId.split("#")[0]);
        int subCategoryIndex = subCategoryId.contains("#") ? Integer.parseInt(subCategoryId.split("#")[1]) : -1;
        return subCategoryIndex != -1 ?
                system.activity.getResources().getStringArray(R.array.categories)[categoryIndex].split("\\|")[1].split(",")[subCategoryIndex]
                :
                system.activity.getResources().getStringArray(R.array.categories)[0];
    }


    private static String getOfConcepts() {
        HashMap<String, Integer> concepts = getConcepts();

        String separator = " or ";
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(");

        for (String key : concepts.keySet())
            if (sentence.contains(key))
                stringBuilder.append("conceptId = ").append(concepts.get(key)).append(separator);

        if (stringBuilder.length() > 1) stringBuilder = new StringBuilder(stringBuilder.substring(0, stringBuilder.length() - separator.length()));
        stringBuilder.append(")");
        if (stringBuilder.length() > 2) return stringBuilder.toString();
        else return null;
    }

    @SuppressLint("Recycle")
    private static HashMap<String, Integer> getConcepts() {
        HashMap<String, Integer> map = new HashMap<>();
        try {
            system.connect();
            Cursor c = system.read().rawQuery("select id, name from concepts where accountId = ?", new String[]{system.def});
            while (c.moveToNext()) map.put(c.getString(1).toLowerCase(), c.getInt(0));
        } catch (Exception ignored) { }
        return map;
    }

    @SuppressLint("Recycle")
    public static String getConceptName(int conceptId) {
        try {
            system.connect();
            Cursor c = system.read().rawQuery("select name from concepts where id = ?", new String[]{String.valueOf(conceptId)});
            if (c.moveToFirst()) return c.getString(0);
        } catch (Exception ignored) { }
        return null;
    }

    private static String getFriends() {
        ArrayList<String> lookUpKeys = null;
        if (Arrays.stream(sentence.split(" ")).anyMatch(VoiceAssistant::isName)) {
            ArrayList<String> names = Arrays.stream(sentence.split(" ")).filter(VoiceAssistant::isName).collect(Collectors.toCollection(ArrayList::new));
            lookUpKeys = new ArrayList<>();
            for (String name : names) lookUpKeys.addAll(findContactsByName(name));
        }
        if (lookUpKeys != null) return "location in(" + arrayJoin(lookUpKeys, ",", true) + ")";
        return null;
    }

    private static String getLocation() {
        ArrayList<String> words = getWords(sentence);
        if (words != null && words.size() > 0 && words.stream().anyMatch(VoiceAssistant::isName)) {
            ArrayList<String> places = words.stream().filter(VoiceAssistant::isName).collect(Collectors.toCollection(ArrayList::new));
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("(");
            String separator = " or ";
            for (String place : places) stringBuilder.append("location like '%").append(place).append("%'").append(separator);
            stringBuilder = new StringBuilder(stringBuilder.substring(0, stringBuilder.length() - separator.length()));
            stringBuilder.append(")");
            return stringBuilder.toString();
        }
        return null;
    }

    //////////////
    // Contacts //
    //////////////
    @SuppressLint("Recycle")
    public static String findContactByLookupKey(String lookupKey) {
        @SuppressLint("Recycle") ContentProviderClient contentProviderClient = system.activity.getContentResolver().acquireContentProviderClient(ContactsContract.Contacts.CONTENT_URI);

        @SuppressLint("Recycle") Cursor cur = null;
        try {
            cur = contentProviderClient.query(ContactsContract.Contacts.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY + " LIKE ?",
                    new String[]{lookupKey + "%"},
                    null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return cur != null && cur.moveToFirst() ? cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)) : "";
    }

    private static ArrayList<String> findContactsByName(String name) {
        ArrayList<String> lookUpKeys = new ArrayList<>();

        @SuppressLint("Recycle") ContentProviderClient mCProviderClient = system.activity.getContentResolver().acquireContentProviderClient(ContactsContract.Contacts.CONTENT_URI);

        try {
            Cursor c = mCProviderClient.query(ContactsContract.Contacts.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " LIKE ?", new String[]{name + "%"},
                    null);
            if (c != null && c.getCount() > 0) {
                while (c.moveToNext()) {
                    if (c.getInt(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                        // Get lookUpKey
                        String lookUpKey = c.getString(c.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY)).split("\\.")[0].split("-")[0];

                        // Add lookUpKey to list
                        lookUpKeys.add("'" + lookUpKey + "'");
                    }
                }
            }

            if (c != null) c.close();
        } catch (RemoteException ignored) { }

        return lookUpKeys;
    }

    //////////////////
    // TRANSACTIONS //
    //////////////////
    public static int offset = 0;
    private static final int startLimit = 20;
    private static final int limit = (startLimit / 2);
    public static int tGroupBy = 2; // [ 0 -> Group | 1 -> Friend | 2 -> Payment ]
    private static int tSentence = -1;
    //////////////////////////////////

    private static TransactionsAdapter getNewTAdapter() {
        return new TransactionsAdapter(system, getTransactions(null));
    }

    @SuppressLint("Recycle")
    private static ArrayList<Object> groupTransactions() {
        ArrayList<Object> friends = new ArrayList<>();
        if (tGroupBy != 0 && ((TransactionsAdapter) recyclerView.getAdapter()).objects != null) {
            if (((TransactionsAdapter) recyclerView.getAdapter()).objects.stream().allMatch(p ->  p.getClass() == Friend.class || p.getClass() == Group.class)) {
                friends.addAll(((TransactionsAdapter) recyclerView.getAdapter()).objects);
            } else if (((TransactionsAdapter) recyclerView.getAdapter()).objects.stream().allMatch(p ->  p.getClass() == Payment.class)) {
                tGroupBy = 1;
                for (Object o : ((TransactionsAdapter) recyclerView.getAdapter()).objects) {
                    Payment p = (Payment) o;
                    if (friends.stream().anyMatch(f -> ((Friend)f).getLookUpKey().equals(p.getLookUpKey()))) {
                        Friend friend = (Friend) friends.stream().filter(f -> ((Friend)f).getLookUpKey().equals(p.getLookUpKey())).findFirst().get();
                        friend.payments.add(p);
                    } else {
                        Friend f = new Friend();
                        f.setLookUpKey(p.getLookUpKey());
                        f.payments = new ArrayList<>();
                        f.payments.add(p);
                        friends.add(f);
                    }
                }
            }
        }
        return friends;
    }

    @SuppressLint("Recycle")
    private static ArrayList<Object> ungroupTransactions() {
        ArrayList<Object> payments = new ArrayList<>();
        if (tGroupBy != 0 && ((TransactionsAdapter) recyclerView.getAdapter()).objects != null) {
            if (((TransactionsAdapter) recyclerView.getAdapter()).objects.stream().allMatch(p ->  p.getClass() == Payment.class || p.getClass() == Group.class)) {
                payments.addAll(((TransactionsAdapter) recyclerView.getAdapter()).objects);
            } else if (((TransactionsAdapter) recyclerView.getAdapter()).objects.stream().allMatch(p ->  p.getClass() == Friend.class)) {
                tGroupBy = 2; tSentence = -1;
                for (Object o : ((TransactionsAdapter) recyclerView.getAdapter()).objects) {
                    Friend f = (Friend) o;
                    payments.addAll(f.payments);
                }
                return payments.stream()
                        .sorted(Comparator.comparingLong(o ->
                                LocalDateTime.parse(((Payment)o).getDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                                        .atZone(ZoneId.systemDefault()).toInstant().getEpochSecond()).reversed())
                        .collect(Collectors.toCollection(ArrayList::new));
            }
        }
        return payments;
    }

    @SuppressLint("Recycle")
    private static ArrayList<Object> showGroups() {
        Cursor c = system.read().rawQuery("select id, location, date, value," +
                "(select sum(value) from movements where conceptId = m.id and type = 'TR') as p," +
                "(select sum(value) from movements where conceptId = m.id and type in('TR','HA')) as t " +
                "from movements m where accountId = ? and type = 'GP' and p = t " +
                "order by date desc, t desc " +
                "limit ? offset ?"
                , new String[]{system.def, String.valueOf(offset == 0 ? startLimit : limit), String.valueOf(offset)});
        ArrayList<Object> groups = new ArrayList<>();
        while (c.moveToNext()) {
            // Initialize group
            Group group = new Group();

            // Set id
            group.setId(c.getInt(0));

            // Set name
            group.setName(c.getString(1));

            // Set date
            group.setDate(c.getString(2));

            // Set value [ my share to pay ]
            group.setValue(c.getDouble(3));

            // Set paid
            group.setPaid(!c.isNull(4) ? c.getDouble(4) : 0);

            // Set total
            group.setTotal(c.getDouble(5));

            // Initialize friends
            group.friends = getFriends(group.getId());

            // Add group
            groups.add(group);

            offset++;
        }
        return groups;
    }

    private static ArrayList<Friend> getFriends(int groupId) {
        @SuppressLint("Recycle") Cursor c = system.read().rawQuery("select location, id, min(date), sum(value), type = 'TR' " +
                "from movements m where accountId = ? and " +
                "type in('SH','HA','TR') and conceptId = ? " +
                "group by location order by date desc, value desc", new String[]{system.def, String.valueOf(groupId)});
        ArrayList<Friend> friends = new ArrayList<>();
        while (c.moveToNext()) {
            // Initialize friend
            Friend friend = new Friend();

            // Set lookup key
            friend.setLookUpKey(c.getString(0));

            // Initialize payments
            friend.payments = new ArrayList<>();

            // Initialize payment
            Payment payment = new Payment();

            // Set id
            payment.setId(c.getInt(1));

            // Set groupId
            payment.setGroupId(groupId);

            // Set date
            payment.setDate(c.getString(2));

            // Set value
            payment.setValue(c.getDouble(3));

            // Set transaction
            payment.setTransaction(c.getInt(4) == 1);

            // Add payment
            friend.payments.add(payment);

            // Add friend
            friends.add(friend);
        }
        return friends;
    }

    @SuppressLint("Recycle")
    private static ArrayList<Object> getTransactions(String selection) {
        tGroupBy = 2;
        Cursor c = system.read().rawQuery("select id,location,conceptId,date,value from movements where accountId = ? and type = 'TR'" +
                (selection != null ? " and " + selection : "") + // Selection
                " order by date desc, abs(value) desc" + // Order by
                " limit ? offset ?" // Limit + Offset
                , new String[]{system.def, String.valueOf(offset == 0 ? startLimit : limit), String.valueOf(offset)});
        ArrayList<Object> payments = new ArrayList<>();
        while (c.moveToNext()) {
            Payment p = new Payment(c.getInt(0),
                    c.getString(1),
                    c.isNull(2) ? -1 : c.getInt(2),
                    c.getString(3),
                    c.getDouble(4),
                    true);
            payments.add(p);
            offset++;
        }
        return payments;
    }

    @SuppressLint("Recycle")
    private static ArrayList<Object> getTransactions() {
        switch (tSentence) {
            case 0: return groupTransactions();
            case 1: return ungroupTransactions();
            case 2: return showGroups();
            default: return getTransactions(getTSelection());
        }
    }

    private static int tSentenceTranslation(String sentence) {
        String[] group = {"group","agrupa","agrupar"};
        String[] ungroup = {"ungroup","desagrupa","desagrupar"};
        String[] showGroups = {"show groups","show group","ver grupos","ver grupo"};
        if (equals(sentence, false, group)) return 0;
        else if (equals(sentence, false, ungroup)) return 1;
        else if (equals(sentence, false, showGroups)) return 2;
        else return -1;
    }

    private static String getTSelection() {
        ArrayList<String> selectionArgs = new ArrayList<>();
        if (sentence != null) {
            selectionArgs.add(getType());
            selectionArgs.add(getValueType());
            selectionArgs.add(getUpperLowerBetweenValueOf());
            selectionArgs.add(getAfterPriorBetweenAgoDate());
            selectionArgs.add(getFriends());
            if (selectionArgs.size() > 0) selectionArgs.removeIf(Objects::isNull);
        }
        return selectionArgs.size() > 0 ? arrayJoin(selectionArgs, " and ", true) : null;
    }

    ///////////////
    // MOVEMENTS //
    ///////////////
    private static MovementsAdapter getNewMAdapter() {
        return new MovementsAdapter(system, getMovements(null));
    }

    @SuppressLint("Recycle")
    private static ArrayList<Object> getMovements(String selection) {
        Cursor c = system.read().rawQuery("select id,conceptId,date,location,subCategoryId,type,value from movements where accountId = ? and " +
                        "type in('EX','IN','I','E')" +
                        (selection != null ? " and " + selection : "") + // Selection
                        " order by date desc, abs(value) desc" + // Order by
                        " limit ? offset ?" // Limit + Offset
                , new String[]{system.def, String.valueOf(offset == 0 ? startLimit : limit), String.valueOf(offset)});
        ArrayList<Object> movements = new ArrayList<>();
        String date = "";
        if (recyclerView.getAdapter() != null && ((MovementsAdapter)recyclerView.getAdapter()).objects.size() > 0) {
            ArrayList<Object> objects = ((MovementsAdapter) recyclerView.getAdapter()).objects;
            Object o = objects.get(objects.size() - 1);
            date = o instanceof Movement ? ((Movement) o).getDate() : (String) o;
        }
        while (c.moveToNext()) {
            if (!date.split(" ")[0].equals(c.getString(2).split(" ")[0])) {
                date = c.getString(2);
                movements.add(date);
            }
            Movement m = new Movement(c.getInt(0),
                    c.isNull(1) ? -1 : c.getInt(1),
                    c.getString(2),
                    c.getString(3),
                    c.getString(4),
                    c.getString(5),
                    c.getDouble(6));
            movements.add(m);
            offset++;
        }
        return movements;
    }

    private static ArrayList<Object> getMovements() {
        return getMovements(getMSelection());
    }

    private static String getMSelection() {
        ArrayList<String> selectionArgs = new ArrayList<>();
        if (sentence != null) {
            selectionArgs.add(getType());
            selectionArgs.add(getValueType());
            selectionArgs.add(getUpperLowerBetweenValueOf());
            selectionArgs.add(getAfterPriorBetweenAgoDate());
            selectionArgs.add(getOfCategories());
            selectionArgs.add(getOfConcepts());
            selectionArgs.add(getLocation());
            if (selectionArgs.size() > 0) selectionArgs.removeIf(Objects::isNull);
        }
        return selectionArgs.size() > 0 ? arrayJoin(selectionArgs, " and ", true) : null;
    }


    ///////////////////////
    // Utility functions //
    ///////////////////////
    // Remove accents
    private static final Map<Character, Character> MAP_NORM;

    static {
        MAP_NORM = new HashMap<>();
        MAP_NORM.put('á', 'a');
        MAP_NORM.put('é', 'e');
        MAP_NORM.put('í', 'i');
        MAP_NORM.put('ó', 'o');
        MAP_NORM.put('ú', 'u');
        MAP_NORM.put('Á', 'A');
        MAP_NORM.put('É', 'E');
        MAP_NORM.put('Í', 'I');
        MAP_NORM.put('Ó', 'O');
        MAP_NORM.put('Ú', 'U');
    }

    public static String removeAccents(String s) {
        if (s == null || s.length() == 0) return null;
        StringBuilder sb = new StringBuilder(s);

        for (int i = 0; i < s.length(); i++) {
            Character c = MAP_NORM.get(sb.charAt(i));
            if (c != null) {
                sb.setCharAt(i, c);
            }
        }

        return sb.toString();
    }
    //////////////////////////////////////////////////

    /////////////////////
    // Payment & Group //
    /////////////////////
    public static boolean isNumber(String s) {
        if (s == null || s.length() == 0) return false;
        return TextUtils.isDigitsOnly(s);
    }

    public static boolean isName(String s) {
        if (s == null || s.length() == 0) return false;
        if (s.chars().allMatch(Character::isLetter)) {
            if (Character.isUpperCase(s.charAt(0))) {
                if (s.length() > 1)
                    for (int x = 1; x < s.length(); x++)
                        if (Character.isUpperCase(s.charAt(x))) return false;
                return true;
            }
        }
        return false;
    }

    public static boolean itsCorrect(String s) {
        if (s == null || s.length() == 0) return false;

        String sentence = removeAccents(s).toLowerCase();
        List<String> words = Arrays.asList(sentence.split(" "));
        return words.stream().anyMatch(w ->
                w.equals("ok") || w.equals("okey") || w.equals("good") || w.equals("nice") || w.equals("well") || w.equals("yes") ||
                        w.equals("va") || w.equals("vale") || w.equals("bien") || w.equals("si") ||
                        w.equals("joan") || w.equals("ados") || w.equals("ondo") || w.equals("bai") ||
                        w.equals(system.activity.getString(R.string.accept).toLowerCase()));
    }

    public static boolean itsCancel(String s) {
        if (s == null || s.length() == 0) return false;

        String sentence = removeAccents(s).toLowerCase();
        List<String> words = Arrays.asList(sentence.split(" "));
        return words.stream().anyMatch(w -> w.startsWith("cancel") || w.equals("utzi"));
    }

    public static double getValue(String s, boolean first) {
        if (s == null || s.length() == 0) return 0;
        String sentence = s.toLowerCase();
        switch (Locale.getDefault().getISO3Language().toLowerCase()) {
            case "eng":
                sentence = sentence.replace(",", "")
                        .replace(".", ",")
                        .replace(" with ", ",")
                        .replace("comma", ",")
                        .replace("hundred", "100")
                        .replace(" millions", "000000")
                        .replace(" million", "000000")
                        .replace("million", "1000000")
                        .replace(" thousand", "000")
                        .replace("thousand", "1000")
                        .replace("a", "1");
                break;
            case "spa":
                sentence = sentence.replace(".", "")
                        .replace(" con ", ",")
                        .replace("coma", ",")
                        .replace("cien", "100")
                        .replace(" millones", "000000")
                        .replace(" millon", "000000")
                        .replace("millon", "1000000")
                        .replace(" mil", "000")
                        .replace("mil", "1000")
                        .replace("un", "1")
                        .replace("una", "1");
                break;
            default: return 0;
        }

        String[] numbers = system.activity.getResources().getStringArray(R.array.numbers);
        for (int i = 0; i < numbers.length; i++)
            sentence = sentence.replace(numbers[i].toLowerCase(), String.valueOf(i));


        String[] strings = sentence.split(",");

        if (strings.length == 2)
            if (TextUtils.isDigitsOnly(strings[1]) && strings[1].length() == 1)
                sentence = sentence.replace("," + strings[1], ",0" + strings[1]);


        String[] words = sentence.split(" ");
        if (first) {
            String word = words[1];
            if (TextUtils.isDigitsOnly(word.replaceFirst(",", ""))) {
                try {
                    return Double.parseDouble(word.replace(",", "."));
                } catch (NumberFormatException ignored) { }
            }
        } else {
            for (String word : words) {
                if (TextUtils.isDigitsOnly(word.replaceFirst(",", ""))) {
                    try {
                        return Double.parseDouble(word.replace(",", "."));
                    } catch (NumberFormatException ignored) { }
                }
            }
        }
        return 0;
    }

    private static double getNextValueOfWords(String sentence, String... words) {
        if (sentence == null || sentence.length() == 0 || words == null || words.length == 0) return 0;
        double value = 0;
        ArrayList<String> strings = getWords(sentence);
        int idx = strings.indexOf(words[0]);
        if (idx != -1 && idx < strings.size() - 1) value = getValue(arrayJoin(getSubWords(getWords(sentence), idx), " ", true), true);
        return value;
    }

    public static double getGroupValue(String s) {
        if (s == null || s.length() == 0) return 0;

        String sentence = VoiceAssistant.removeAccents(s).toLowerCase();
        switch (System.getSystemISO3Language()) {
            case "eng":
                sentence = sentence.replaceAll(" between ", " / ");
                break;
            case "spa":
                sentence = sentence.replaceAll(" entre ", " / ");
                break;
            default: return 0;
        }

        String[] strings = sentence.split(" / ");

        if (strings.length == 2 && strings[0].length() > 0) {
            String[] words = strings[0].split(" ");
            for (String word : words) {
                double value = getValue(word, false);
                if (value > 0) return value;
            }
        }
        return 0;
    }

    public static ArrayList<String> getParticipantNames(String s) {
        if (s == null || s.length() == 0) return null;
        String sentence = VoiceAssistant.removeAccents(s);
        switch (System.getSystemISO3Language()) {
            case "eng":
                sentence = sentence.replaceAll(" between ", " / ");
                break;
            case "spa":
                sentence = sentence.replaceAll(" entre ", " / ");
                break;
            default: return null;
        }

        String[] strings = sentence.split(" / ");

        if (strings.length == 2 && strings[0].length() > 0) {
            String[] words = strings[1].split(" ");
            ArrayList<String> names = new ArrayList<>();
            for (String word : words)
                if (isName(word)) names.add(word);
            if (names.size() > 0) return names;
        }
        return null;
    }

    public static int getNumberOfParticipants(String s) {
        if (s == null || s.length() == 0) return 0;
        String sentence = removeAccents(s);
        if (!sentence.contains(" / ")) {
            if (!sentence.contains(" entre ")) { sentence = sentence.replaceAll(" between ", " entre "); }
        } else { sentence = sentence.replaceAll(" / ", " entre "); }

        String[] strings = sentence.split(" entre ");

        if (strings.length == 2 && strings[1].length() > 0) {
            String[] words = strings[1].split(" ");
            for (String word : words)
                if (isNumber(word) && System.tryParseInt(word, 0) != 0) return System.tryParseInt(word, 0);
        }
        return 0;
    }
    /////////////////////////////////////////////////////////

    ////////////////////////
    // ArrayList & String //
    ////////////////////////
    // From ArrayList to String
    public static String arrayJoin(ArrayList<String> strings, String separator, boolean removeLastSeparator) {
        if (strings == null || strings.size() == 0) return null;
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : strings) stringBuilder.append(s).append(separator);
        if (removeLastSeparator) stringBuilder = new StringBuilder(stringBuilder.substring(0, stringBuilder.length() - separator.length()));
        return stringBuilder.toString();
    }

    // From String to ArrayList
    private static ArrayList<String> getWords(String sentence) {
        if (sentence == null || sentence.length() == 0) return null;
        return new ArrayList<>(Arrays.asList(sentence.split(" ")));
    }

    // Sub words [ startIndex ]
    private static ArrayList<String> getSubWords(ArrayList<String> words, int startIndex) {
        if (words == null || words.size() == 0 || startIndex < 0 || startIndex >= words.size()) return null;
        ArrayList<String> strings = new ArrayList<>();
        for (int i = startIndex; i < words.size(); i++) strings.add(words.get(i));
        return strings;
    }

    // Sub words [ startIndex - endIndex ]
    private static ArrayList<String> getSubWords(ArrayList<String> words, int startIndex, int endIndex) {
        if (words == null || words.size() == 0 || startIndex < 0 || startIndex >= words.size()) return null;
        ArrayList<String> strings = new ArrayList<>();
        for (int i = startIndex; i < words.size() && i < endIndex; i++) strings.add(words.get(i));
        return strings;
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    ///////////////////////
    // Contains & Equals //
    ///////////////////////
    // Contains string? [ Array ]
    private static boolean contains(String... words) {
        if (words == null || words.length == 0) return false;
        for (String word : words) if (sentence.contains(word)) return true;
        return false;
    }

    // Equals to string? [ Array ]
    private static boolean equals(String word, boolean keySensitive, String... words) {
        if (words == null || words.length == 0 || word == null) return false;
        for (String s : words)
            if (keySensitive && word.equals(s) || !keySensitive && word.toLowerCase().equals(s.toLowerCase()))
                return true;
        return false;
    }

    // Equals to string? [ ArrayList ]
    private static boolean equals(String word, boolean keySensitive, ArrayList<String> words) {
        if (words == null || words.size() == 0 || word == null) return false;
        for (int i = 0; i < words.size(); i++) if (keySensitive && word.equals(words.get(i)) || !keySensitive && word.toLowerCase().equals(words.get(i).toLowerCase())) return true;
        return false;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////

    ///////////
    // Index //
    ///////////
    // Index of object [ Equals ]
    private static int indexOf(ArrayList<String> words, boolean keySensitive, String word) {
        if (words == null || words.size() == 0 || word == null) return -1;
        int idx = -1;
        if (keySensitive) {
            if (words.stream().anyMatch(s -> s.equals(word)))
                idx = words.indexOf(words.stream().filter(s -> s.equals(word)).findFirst().get());
        } else {
            if (words.stream().anyMatch(s -> s.toLowerCase().equals(word.toLowerCase())))
                idx = words.indexOf(words.stream().filter(s -> s.toLowerCase().equals(word.toLowerCase())).findFirst().get());
        }
        return idx;
    }

    // Index of object [ Start with ]
    private static int startWithIndexOf(ArrayList<String> words, boolean keySensitive, String word) {
        if (words == null || words.size() == 0 || word == null) return -1;
        int idx = -1;
        if (keySensitive) {
            if (words.stream().anyMatch(word::startsWith))
                idx = words.indexOf(words.stream().filter(word::startsWith).findFirst().get());
        } else {
            if (words.stream().anyMatch(s -> word.toLowerCase().startsWith(removeAccents(s).toLowerCase())))
                idx = words.indexOf(words.stream().filter(s -> word.toLowerCase().startsWith(removeAccents(s).toLowerCase())).findFirst().get());
        }
        return idx;
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////

    ///////////////////////
    // SQLite formatting //
    ///////////////////////
    private static String replace(String sentence, String[] regex, String replacement) {
        if (regex == null || regex.length == 0 || replacement == null) return null;
        String string = sentence;
        for (int i = 1; i < regex.length; i++) string = string.replace(regex[i], replacement);
        return string;
    }

    private static String betweenValues(double firstValue, double secondValue) {
        return "value between abs(" + Math.min(firstValue, secondValue) + ") and abs(" + Math.max(firstValue, secondValue) + ")";
    }

    private static String betweenDates(String firstDate, String secondDate) {
        ArrayList<String> dates = new ArrayList<>();
        dates.add(firstDate);
        dates.add(secondDate);
        //
        LocalDate start = dates.stream().map(date -> LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"))).min(LocalDate::compareTo).get();
        LocalDate end = dates.stream().map(date -> LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"))).max(LocalDate::compareTo).get();
        //
        dates.set(0, start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        dates.set(1, end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        return "(date between date('" + dates.get(0) + "') and date('" + dates.get(1) + "') or date(date) = date('" + dates.get(1) + "'))";
    }

    private static String betweenDates(Calendar firstDate, Calendar secondDate) {
        String strFirstDate = getFormattedDate(firstDate);
        String strSecondDate = getFormattedDate(secondDate);
        //
        ArrayList<String> dates = new ArrayList<>();
        dates.add(strFirstDate);
        dates.add(strSecondDate);
        //
        LocalDate start = dates.stream().map(date -> LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"))).min(LocalDate::compareTo).get();
        LocalDate end = dates.stream().map(date -> LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"))).max(LocalDate::compareTo).get();
        //
        dates.set(0, start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        dates.set(1, end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        return "(date between date('" + dates.get(0) + "') and date('" + dates.get(1) + "') or date(date) = date('" + dates.get(1) + "'))";
    }

    private static String equalsToDate(String date) {
        return "date(date) = date('" + date + "')";
    }

    private static String afterToDate(String date) {
        return "date(date) > date('" + date + "')";
    }

    private static String priorToDate(String date) {
        return "date(date) < date('" + date + "')";
    }
    //////////////////////////////////////////////////////////////////////////////////////////////


    //////////
    // Date //
    //////////
    private static ArrayList<String> reformDateTypes(ArrayList<String> words) {
        if (words == null || words.size() == 0) return null;
        ArrayList<String[]> listOfArrays = new ArrayList<>();
        String[] dayType = {"day","days","dia","dias"};
        listOfArrays.add(dayType);
        String[] weekType = {"week","weeks","semana","semanas"};
        listOfArrays.add(weekType);
        String[] fortnightType = {"fortnight","fortnights","quincena","quincenas"};
        listOfArrays.add(fortnightType);
        String[] monthType = {"month","months","mes","meses"};
        listOfArrays.add(monthType);
        String[] seasonType = {"season","seasons","estacion","estaciones"};
        listOfArrays.add(seasonType);
        String[] yearType = {"year","years","año","años"};
        listOfArrays.add(yearType);

        for (int i = 0; i < listOfArrays.size(); i++) {
            for (int j = 0; j < words.size(); j++) {
                if (equals(words.get(j), false, listOfArrays.get(i))) {
                    words.set(j, listOfArrays.get(i)[0]);
                }
            }
        }

        String[] first = {"first","primero","primera"};
        String[] second = {"second","segundo","segunda"};
        String[] third = {"third","tercero","tercera"};
        String[] fourth = {"fourth","cuarto","cuarta"};
        String[] of = {"of","from","de","del"};
        for (int i = 0; i < words.size(); i++) {
            String word = words.get(i);
            if (equals(word, false, first) ||
                    equals(word, false, second) ||
                    equals(word, false, third) ||
                    equals(word, false, fourth)) {
                if (equals(word, false, first)) {
                    words.set(i, "1º");
                } else if (equals(word, false, second)) {
                    words.set(i, "2º");
                } else if (equals(word, false, third)) {
                    words.set(i, "3º");
                } else {
                    words.set(i, "4º");
                }
                if (i < words.size() - 1) {
                    String nextWord = words.get(i + 1);
                    if (equals(nextWord, false, of)) {
                        words.remove(i + 1);
                    }
                }
            }

            if ((equals(word, false, "week") || equals(word, false, "fortnight")) &&
                    i < words.size() - 1) {
                String nextWord = words.get(i + 1);
                if (equals(nextWord, false, of)) {
                    words.remove(i + 1);
                }
            }
        }




        listOfArrays.clear();
        String[] daysOfWeek = system.activity.getResources().getStringArray(R.array.days);
        listOfArrays.add(daysOfWeek);
        String[] monthsOfYear = system.activity.getResources().getStringArray(R.array.months);
        listOfArrays.add(monthsOfYear);
        String[] seasonsOfYear = system.activity.getResources().getStringArray(R.array.seasons);
        listOfArrays.add(seasonsOfYear);

        for (int i = 0; i < listOfArrays.size(); i++) {
            char c;
            if (i == 0) c = 'd';
            else if (i == 1) c = 'm';
            else c = 's';

            for (int j = 0; j < words.size(); j++) {
                if (words.get(j) != null) {
                    if (startWithIndexOf(new ArrayList<>(Arrays.asList(listOfArrays.get(i))), false, words.get(j)) != -1) {
                        if (c == 'd')
                            words.set(j, c + "" + (startWithIndexOf(new ArrayList<>(Arrays.asList(listOfArrays.get(i))), false, words.get(j)) + 1));
                        else {
                            if (j > 0) {
                                String befWord = words.get(j - 1);
                                if (equals(befWord, false, of)) {
                                    words.set(j - 1, null);
                                }
                            }
                            if (j < words.size() - 1) {
                                String nextWord = words.get(j + 1);
                                if (equals(nextWord, false, of)) {
                                    words.set(j + 1, null);
                                }
                            }
                            words.set(j, c + "" + startWithIndexOf(new ArrayList<>(Arrays.asList(listOfArrays.get(i))), false, words.get(j)));
                        }
                    }
                }
            }
            words.removeIf(Objects::isNull);
        }
        return words;
    }

    @SuppressLint("SimpleDateFormat")
    private static String getFormattedDate(Calendar date) {
        return new SimpleDateFormat("yyyy-MM-dd").format(date.getTime());
    }

    private static String getDate(ArrayList<String> words, boolean SQLiteFormat) {
        // Before yesterday: DONE
        if (isBeforeYesterday(words, SQLiteFormat) != null)
            return isBeforeYesterday(words, SQLiteFormat);

        // Yesterday: DONE
        if (isYesterday(words, SQLiteFormat) != null)
            return isYesterday(words, SQLiteFormat);

        // Today: DONE
        if (isToday(words, SQLiteFormat) != null)
            return isToday(words, SQLiteFormat);

        // Day of week: DONE
        if (isDayOfWeek(words, SQLiteFormat) != null)
            return isDayOfWeek(words, SQLiteFormat);

        // Day of month · only available* in spanish: DONE
        if (isDayOfMonth(words, SQLiteFormat) != null)
            return isDayOfMonth(words, SQLiteFormat);

        // X Week: DONE
        if (isXWeek(words, SQLiteFormat) != null)
            return isXWeek(words, SQLiteFormat);

        // Week: DONE
        if (isWeek(words, SQLiteFormat) != null)
            return isWeek(words, SQLiteFormat);

        // X Fortnight: DONE
        if (isXFortnight(words, SQLiteFormat) != null)
            return isXFortnight(words, SQLiteFormat);

        // Fortnight: DONE
        if (isFortnight(words, SQLiteFormat) != null)
            return isFortnight(words, SQLiteFormat);

        // X Month: DONE
        if (isXMonth(words, SQLiteFormat) != null)
            return isXMonth(words, SQLiteFormat);

        // Month: DONE
        if (isMonth(words, SQLiteFormat) != null)
            return isMonth(words, SQLiteFormat);

        // X Season: DONE
        if (isXSeason(words, SQLiteFormat) != null)
            return isXSeason(words, SQLiteFormat);

        // Season: DONE
        if (isSeason(words, SQLiteFormat) != null)
            return isSeason(words, SQLiteFormat);

        // X Year: DONE
        if (isXYear(words, SQLiteFormat) != null)
            return isXYear(words, SQLiteFormat);

        // Year: DONE
        if (isYear(words, SQLiteFormat) != null)
            return isYear(words, SQLiteFormat);
        return null;
    }

    private static String isBeforeYesterday(ArrayList<String> worlds, boolean SQLiteFormat) {
        if (worlds == null || worlds.size() == 0) return null;
        if (equals("before_yesterday", false, worlds)) {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, -2);
            return SQLiteFormat ? equalsToDate(getFormattedDate(c)) : getFormattedDate(c);
        }
        return null;
    }

    private static String isYesterday(ArrayList<String> worlds, boolean SQLiteFormat) {
        if (worlds == null || worlds.size() == 0) return null;
        if (equals("yesterday", false, worlds)) {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, -1);
            return SQLiteFormat ? equalsToDate(getFormattedDate(c)) : getFormattedDate(c);
        }
        return null;
    }

    private static String isToday(ArrayList<String> worlds, boolean SQLiteFormat) {
        if (worlds == null || worlds.size() == 0) return null;
        if (equals("today", false, worlds)) {
            Calendar c = Calendar.getInstance();
            return SQLiteFormat ? equalsToDate(getFormattedDate(c)) : getFormattedDate(c);
        }
        return null;
    }

    private static String isDayOfWeek(ArrayList<String> words, boolean SQLiteFormat) {
        if (words == null || words.size() == 0) return null;
        String[] last = {"last","pasado","pasada"};
        Calendar c;
        if (words.stream().anyMatch(w -> w.length() == 2 && w.charAt(0) == 'd' && TextUtils.isDigitsOnly(w.substring(1)))) {
            String word = words.stream().filter(w -> w.length() == 2 && w.charAt(0) == 'd' && TextUtils.isDigitsOnly(w.substring(1))).findFirst().get();
            int idx = words.indexOf(word);
            int day = Integer.parseInt(word.substring(1));
            c = getDayOfWeek(day);
            if (c != null) {
                if (idx > 0) {
                    String befWord = words.get(idx - 1);
                    if (equals(befWord, false, last)) {
                        c.add(Calendar.DATE, -7);
                    }
                }

                if (idx < words.size() - 1) {
                    String nextWord = words.get(idx + 1);
                    if (equals(nextWord, false, last)) {
                        c.add(Calendar.DATE, -7);
                    }
                }
                if (SQLiteFormat) return equalsToDate(getFormattedDate(c));
                else return getFormattedDate(c);
            }
        }
        return null;
    }

    private static String isDayOfMonth(ArrayList<String> words, boolean SQLiteFormat) {
        if (words == null || words.size() == 0) return null;
        String[] of = {"of","from","the","el","de","del"};
        Calendar c = null;
        int day = -1;
        if (words.stream().anyMatch(w -> equals(w, false, of))) {
            for (int i = 0; i < words.size(); i++) {
                String word = words.get(i);
                // from...
                if (equals(word, false, of)) {
                    if (i < words.size() - 1) {
                        String nextWord = words.get(i + 1);
                        // ...day [dd]...
                        if (nextWord.length() <= 2 && TextUtils.isDigitsOnly(nextWord)) {
                            try {  day = Integer.parseInt(nextWord); } catch (NumberFormatException ignored) { }
                            if (day != -1 && i < words.size() - 2) {
                                nextWord = words.get(i + 2);
                                // ...month [MM]...
                                if (nextWord.length() >= 2 && nextWord.length() <= 3 && nextWord.charAt(0) == 'm' && TextUtils.isDigitsOnly(nextWord.substring(1))) {
                                    int month = -1;
                                    try {  month = Integer.parseInt(nextWord.substring(1)); } catch (NumberFormatException ignored) { }
                                    if (month >= 0 && month <= 11) {
                                        c = Calendar.getInstance();
                                        c.set(Calendar.MONTH, month);
                                        if (i < words.size() - 3) {
                                            nextWord = words.get(i + 3);
                                            // ...year [yyyy]
                                            if (nextWord.length() == 4 && TextUtils.isDigitsOnly(nextWord)) {
                                                int year = -1;
                                                try {  year = Integer.parseInt(nextWord); } catch (NumberFormatException ignored) { }
                                                if (year != -1) {
                                                    c.set(Calendar.YEAR, year);
                                                }
                                            }
                                            // ...year [last_year]
                                            else if (nextWord.equals("last_year")) {
                                                c.add(Calendar.YEAR, -1);
                                            }
                                        }
                                    }
                                } else {
                                    // ...month [last_month]...
                                    if (words.contains("last_month")) {
                                        c = Calendar.getInstance();
                                        c.add(Calendar.MONTH, -1);
                                        int idx = words.indexOf("last_month");
                                        if (idx != -1 && idx < words.size() - 1) {
                                            nextWord = words.get(idx + 1);
                                            // ...year [yyyy]
                                            if (nextWord.length() == 4 && TextUtils.isDigitsOnly(nextWord)) {
                                                int year = -1;
                                                try {  year = Integer.parseInt(nextWord); } catch (NumberFormatException ignored) { }
                                                if (year != -1) {
                                                    c.set(Calendar.YEAR, year);
                                                }
                                            }
                                            // ...year [last_year]
                                            else if (nextWord.equals("last_year")) {
                                                c.add(Calendar.YEAR, -1);
                                            }
                                            // ...from
                                            else if (equals(word, false, of) && idx < words.size() - 2) {
                                                nextWord = words.get(idx + 2);
                                                // ...year [yyyy]
                                                if (nextWord.length() == 4 && TextUtils.isDigitsOnly(nextWord)) {
                                                    int year = -1;
                                                    try {  year = Integer.parseInt(nextWord); } catch (NumberFormatException ignored) { }
                                                    if (year != -1) {
                                                        c.set(Calendar.YEAR, year);
                                                    }
                                                }
                                                // ...year [last_year]
                                                else if (nextWord.equals("last_year")) {
                                                    c.add(Calendar.YEAR, -1);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (c == null && day != -1) {
            c = Calendar.getInstance();
            if (day > c.getActualMaximum(Calendar.DAY_OF_MONTH)) c = null;
        }
        if (c != null) {
            if (day >= 1 && day <= c.getActualMaximum(Calendar.DAY_OF_MONTH)) c.set(Calendar.DAY_OF_MONTH, day);
            if (SQLiteFormat) return equalsToDate(getFormattedDate(c));
            else return getFormattedDate(c);
        }
        return null;
    }

    private static String isXWeek(ArrayList<String> words, boolean SQLiteFormat) {
        if (words == null || words.size() == 0) return null;
        String[] of = {"from","de","del"};
        Calendar c;
        Calendar cEnd = null;
        if (words.contains("week")) {
            int idx = indexOf(words, false, "week");
            c = Calendar.getInstance();
            if (idx < words.size() - 1) {
                String nextWord = words.get(idx + 1);
                if (nextWord.equals("last_month")) {
                    c.add(Calendar.MONTH, -1);
                    if (idx < words.size() - 2) {
                        nextWord = words.get(idx + 2);
                        if (nextWord.length() == 4 && TextUtils.isDigitsOnly(nextWord)) {
                            try {
                                c.set(Calendar.YEAR, Integer.parseInt(nextWord));
                            } catch (NumberFormatException ignored) { }
                        } else if (nextWord.equals("last_year")) c.add(Calendar.YEAR, -1);
                        else if (idx < words.size() - 3) {
                            nextWord = words.get(idx + 3);
                            if (nextWord.length() == 4 && TextUtils.isDigitsOnly(nextWord)) {
                                try {
                                    c.set(Calendar.YEAR, Integer.parseInt(nextWord));
                                } catch (NumberFormatException ignored) { }
                            } else if (nextWord.equals("last_year")) c.add(Calendar.YEAR, -1);
                        }
                    }
                }
                else {
                    if (nextWord.length() >= 2 && nextWord.length() <= 3 && nextWord.charAt(0) == 'm' && TextUtils.isDigitsOnly(nextWord.substring(1))) {
                        try {
                            c.set(Calendar.MONTH, Integer.parseInt(nextWord.substring(1)));
                        } catch (NumberFormatException ignored) { }
                    }

                    if (idx < words.size() - 2) {
                        nextWord = words.get(idx + 2);
                        if (nextWord.length() == 4 && TextUtils.isDigitsOnly(nextWord)) {
                            try {
                                c.set(Calendar.YEAR, Integer.parseInt(nextWord));
                            } catch (NumberFormatException ignored) { }
                        } else if (equals(nextWord, false, of) && idx < words.size() - 3) {
                            nextWord = words.get(idx + 3);
                            if (nextWord.length() == 4 && TextUtils.isDigitsOnly(nextWord)) {
                                try {
                                    c.set(Calendar.YEAR, Integer.parseInt(nextWord));
                                } catch (NumberFormatException ignored) { }
                            } else if (nextWord.equals("last_year")) c.add(Calendar.YEAR, -1);
                        } else if (nextWord.equals("last_month")) {
                            c.add(Calendar.MONTH, -1);
                            if (idx < words.size() - 3) {
                                nextWord = words.get(idx + 3);
                                if (nextWord.length() == 4 && TextUtils.isDigitsOnly(nextWord)) {
                                    try {
                                        c.set(Calendar.YEAR, Integer.parseInt(nextWord));
                                    } catch (NumberFormatException ignored) { }
                                } else if (nextWord.equals("last_year")) c.add(Calendar.YEAR, -1);
                                else if (idx < words.size() - 4) {
                                    nextWord = words.get(idx + 4);
                                    if (nextWord.length() == 4 && TextUtils.isDigitsOnly(nextWord)) {
                                        try {
                                            c.set(Calendar.YEAR, Integer.parseInt(nextWord));
                                        } catch (NumberFormatException ignored) { }
                                    } else if (nextWord.equals("last_year")) c.add(Calendar.YEAR, -1);
                                }
                            }
                        }
                    }
                }
            }

            if (idx > 0) {
                String befWord = words.get(idx - 1);
                if (equals(befWord, false, "1º","2º","3º","4º")) {
                    switch (befWord) {
                        case "1º":
                            c.set(Calendar.DAY_OF_MONTH, 1);
                            if (SQLiteFormat) {
                                cEnd = Calendar.getInstance();
                                cEnd.setTime(c.getTime());
                                cEnd.set(Calendar.DATE, 7);
                            }
                            break;
                        case "2º":
                            c.set(Calendar.DAY_OF_MONTH, 8);
                            if (SQLiteFormat) {
                                cEnd = Calendar.getInstance();
                                cEnd.setTime(c.getTime());
                                cEnd.set(Calendar.DATE, 14);
                            }
                            break;
                        case "3º":
                            c.set(Calendar.DAY_OF_MONTH, 15);
                            if (SQLiteFormat) {
                                cEnd = Calendar.getInstance();
                                cEnd.setTime(c.getTime());
                                cEnd.set(Calendar.DATE, 21);
                            }
                            break;
                        default:
                            c.set(Calendar.DAY_OF_MONTH, 22);
                            if (SQLiteFormat) {
                                cEnd = Calendar.getInstance();
                                cEnd.setTime(c.getTime());
                                cEnd.set(Calendar.DATE, c.getActualMaximum(Calendar.DAY_OF_MONTH));
                            }
                            break;
                    }
                } else if (equals(befWord, false,"last","ultima")) {
                    c.set(Calendar.DAY_OF_MONTH, 22);
                    if (SQLiteFormat) {
                        cEnd = Calendar.getInstance();
                        cEnd.setTime(c.getTime());
                        cEnd.set(Calendar.DATE, c.getActualMaximum(Calendar.DAY_OF_MONTH));
                    }
                }
                else {
                    c = null;
                    cEnd = null;
                }
            } else {
                c = null;
                cEnd = null;
            }
            if (c != null && cEnd != null) return betweenDates(c, cEnd);
            else if (c != null) return getFormattedDate(c);
        }
        return null;
    }

    private static String isWeek(ArrayList<String> words, boolean SQLiteFormat) {
        if (words == null || words.size() == 0) return null;
        Calendar c = null;
        Calendar cEnd = null;
        if (words.contains("the_week")) {
             c = Calendar.getInstance();
             c.set(Calendar.DAY_OF_WEEK, 1);
             c.add(Calendar.DAY_OF_WEEK, -6);
             if (SQLiteFormat) {
                 cEnd = Calendar.getInstance();
                 cEnd.setTime(c.getTime());
                 cEnd.add(Calendar.DATE, 6);
             }
        } else if (words.contains("last_week")) {
            c = Calendar.getInstance();
            c.set(Calendar.DAY_OF_WEEK, 1);
            c.add(Calendar.DAY_OF_WEEK, -(6 + 7));
            if (SQLiteFormat) {
                cEnd = Calendar.getInstance();
                cEnd.setTime(c.getTime());
                cEnd.add(Calendar.DATE, 6);
            }
        }
        if (c != null && cEnd != null) return betweenDates(c, cEnd);
        else if (c != null) return getFormattedDate(c);
        else return null;
    }

    private static String isXFortnight(ArrayList<String> words, boolean SQLiteFormat) {
        if (words == null || words.size() == 0) return null;
        String[] of = {"of","from","de","del"};
        Calendar c;
        Calendar cEnd = null;
        if (words.contains("fortnight")) {
            int idx = indexOf(words, false, "fortnight");
            c = Calendar.getInstance();
            if (idx < words.size() - 1) {
                String nextWord = words.get(idx + 1);
                if (nextWord.equals("last_month")) {
                    c.add(Calendar.MONTH, -1);
                    if (idx < words.size() - 2) {
                        nextWord = words.get(idx + 2);
                        if (nextWord.length() == 4 && TextUtils.isDigitsOnly(nextWord)) {
                            try {
                                c.set(Calendar.YEAR, Integer.parseInt(nextWord));
                            } catch (NumberFormatException ignored) { }
                        } else if (nextWord.equals("last_year")) c.add(Calendar.YEAR, -1);
                        else if (idx < words.size() - 3) {
                            nextWord = words.get(idx + 3);
                            if (nextWord.length() == 4 && TextUtils.isDigitsOnly(nextWord)) {
                                try {
                                    c.set(Calendar.YEAR, Integer.parseInt(nextWord));
                                } catch (NumberFormatException ignored) { }
                            } else if (nextWord.equals("last_year")) c.add(Calendar.YEAR, -1);
                        }
                    }
                }
                else {
                    if (nextWord.length() <= 2 && nextWord.charAt(0) == 'm' && TextUtils.isDigitsOnly(nextWord.substring(1))) {
                        try {
                            c.set(Calendar.MONTH, Integer.parseInt(nextWord.substring(1)));
                        } catch (NumberFormatException ignored) { }
                    }

                    if (idx < words.size() - 2) {
                        nextWord = words.get(idx + 2);
                        if (nextWord.length() == 4 && TextUtils.isDigitsOnly(nextWord)) {
                            try {
                                c.set(Calendar.YEAR, Integer.parseInt(nextWord));
                            } catch (NumberFormatException ignored) { }
                        } else if (nextWord.equals("last_year")) c.add(Calendar.YEAR, -1);
                        else if (equals(nextWord, false, of) && idx < words.size() - 3) {
                            nextWord = words.get(idx + 3);
                            if (nextWord.length() == 4 && TextUtils.isDigitsOnly(nextWord)) {
                                try {
                                    c.set(Calendar.YEAR, Integer.parseInt(nextWord));
                                } catch (NumberFormatException ignored) { }
                            } else if (nextWord.equals("last_year")) c.add(Calendar.YEAR, -1);
                        } else if (nextWord.equals("last_month")) {
                            c.add(Calendar.MONTH, -1);
                            if (idx < words.size() - 3) {
                                nextWord = words.get(idx + 3);
                                if (nextWord.length() == 4 && TextUtils.isDigitsOnly(nextWord)) {
                                    try {
                                        c.set(Calendar.YEAR, Integer.parseInt(nextWord));
                                    } catch (NumberFormatException ignored) { }
                                } else if (nextWord.equals("last_year")) c.add(Calendar.YEAR, -1);
                                else if (idx < words.size() - 4) {
                                    nextWord = words.get(idx + 4);
                                    if (nextWord.length() == 4 && TextUtils.isDigitsOnly(nextWord)) {
                                        try {
                                            c.set(Calendar.YEAR, Integer.parseInt(nextWord));
                                        } catch (NumberFormatException ignored) { }
                                    } else if (nextWord.equals("last_year")) c.add(Calendar.YEAR, -1);
                                }
                            }
                        }
                    }
                }
            }

            if (idx > 0) {
                String befWord = words.get(idx - 1);
                if (equals(befWord, false, "1º","2º")) {
                    if (befWord.equals("1º")) {
                        c.set(Calendar.DAY_OF_MONTH, 1);
                        if (SQLiteFormat) {
                            cEnd = Calendar.getInstance();
                            cEnd.setTime(c.getTime());
                            cEnd.set(Calendar.DATE, 15);
                        }
                    } else {
                        c.set(Calendar.DAY_OF_MONTH, 16);
                        if (SQLiteFormat) {
                            cEnd = Calendar.getInstance();
                            cEnd.setTime(c.getTime());
                            cEnd.set(Calendar.DATE, c.getActualMaximum(Calendar.DAY_OF_MONTH));
                        }
                    }
                } else if (equals(befWord, false, "last","ultima")) {
                    c.set(Calendar.DAY_OF_MONTH, 16);
                    if (SQLiteFormat) {
                        cEnd = Calendar.getInstance();
                        cEnd.setTime(c.getTime());
                        cEnd.set(Calendar.DATE, c.getActualMaximum(Calendar.DAY_OF_MONTH));
                    }
                }
                else {
                    c = null;
                    cEnd = null;
                }
            } else {
                c = null;
                cEnd = null;
            }
            if (c != null && cEnd != null) return betweenDates(c, cEnd);
            else if (c != null) return getFormattedDate(c);
        }
        return null;
    }

    private static String isFortnight(ArrayList<String> words, boolean SQLiteFormat) {
        if (words == null || words.size() == 0) return null;
        Calendar c = null;
        Calendar cEnd = null;
        if (words.contains("the_fortnight")) {
            c = Calendar.getInstance();
            int day = c.get(Calendar.DAY_OF_MONTH);
            if (day > 15) {
                c.set(Calendar.DAY_OF_MONTH, 15);
                if (SQLiteFormat) {
                    cEnd = Calendar.getInstance();
                    cEnd.setTime(c.getTime());
                    cEnd.set(Calendar.DATE, c.getActualMaximum(Calendar.DAY_OF_MONTH));
                }
            } else {
                c.set(Calendar.DAY_OF_MONTH, 1);
                if (SQLiteFormat) {
                    cEnd = Calendar.getInstance();
                    cEnd.setTime(c.getTime());
                    cEnd.set(Calendar.DAY_OF_MONTH, 15);
                }
            }
        } else if (words.contains("last_fortnight")) {
            c = Calendar.getInstance();
            int day = c.get(Calendar.DAY_OF_MONTH);
            if (day > 15) {
                c.set(Calendar.DAY_OF_MONTH, 1);
                if (SQLiteFormat) {
                    cEnd = Calendar.getInstance();
                    cEnd.setTime(c.getTime());
                    cEnd.set(Calendar.DAY_OF_MONTH, 15);
                }
            } else {
                c.add(Calendar.MONTH, -1);
                c.set(Calendar.DAY_OF_MONTH, 15);
                if (SQLiteFormat) {
                    cEnd = Calendar.getInstance();
                    cEnd.setTime(c.getTime());
                    cEnd.set(Calendar.DATE, c.getActualMaximum(Calendar.DAY_OF_MONTH));
                }
            }
        }
        if (c != null && cEnd != null) return betweenDates(c, cEnd);
        else if (c != null) return getFormattedDate(c);
        else return null;
    }

    private static String isXMonth(ArrayList<String> words, boolean SQLiteFormat) {
        if (words == null || words.size() == 0) return null;
        String[] of = {"of","from","de","del"};
        Calendar c;
        Calendar cEnd = null;
        if (words.stream().anyMatch(w -> w.length() >= 2 && w.length() <= 3 && w.charAt(0) == 'm' && TextUtils.isDigitsOnly(w.substring(1)))) {
            c = Calendar.getInstance();
            c.set(Calendar.DAY_OF_MONTH, 1);
            String word = words.stream().filter(w -> w.length() >= 2 && w.length() <= 3 && w.charAt(0) == 'm' && TextUtils.isDigitsOnly(w.substring(1))).findFirst().get();
            int idx = words.indexOf(word);
            try {
                c.set(Calendar.MONTH, Integer.parseInt(word.substring(1)));
            } catch (NumberFormatException ignored) { }
            if (idx < words.size() - 1) {
                String nextWord = words.get(idx + 1);
                if (nextWord.length() == 4 && TextUtils.isDigitsOnly(nextWord)) {
                    try {
                        c.set(Calendar.YEAR, Integer.parseInt(nextWord));
                    } catch (NumberFormatException ignored) { }
                } else if (nextWord.equals("last_year")) c.add(Calendar.YEAR, -1);
                else if (equals(nextWord, false, of) && idx < words.size() - 2) {
                    nextWord = words.get(idx + 2);
                    if (nextWord.length() == 4 && TextUtils.isDigitsOnly(nextWord)) {
                        try {
                            c.set(Calendar.YEAR, Integer.parseInt(nextWord));
                        } catch (NumberFormatException ignored) { }
                    } else if (nextWord.equals("last_year")) c.add(Calendar.YEAR, -1);
                }
            }
            if (SQLiteFormat) {
                cEnd = Calendar.getInstance();
                cEnd.setTime(c.getTime());
                cEnd.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
            }
            if (c != null && cEnd != null) return betweenDates(c, cEnd);
            else if (c != null) return getFormattedDate(c);
        }
        return null;
    }

    private static String isMonth(ArrayList<String> words, boolean SQLiteFormat) {
        if (words == null || words.size() == 0) return null;
        Calendar c = null;
        Calendar cEnd = null;
        if (words.contains("the_month")) {
            c = Calendar.getInstance();
            c.set(Calendar.DAY_OF_MONTH, 1);
            if (SQLiteFormat) {
                cEnd = Calendar.getInstance();
                cEnd.setTime(c.getTime());
                cEnd.set(Calendar.DATE, c.getActualMaximum(Calendar.DAY_OF_MONTH));
            }
        } else if (words.contains("last_month")) {
            c = Calendar.getInstance();
            c.add(Calendar.MONTH, -1);
            c.set(Calendar.DAY_OF_MONTH, 1);
            if (SQLiteFormat) {
                cEnd = Calendar.getInstance();
                cEnd.setTime(c.getTime());
                cEnd.set(Calendar.DATE, c.getActualMaximum(Calendar.DAY_OF_MONTH));
            }
        }
        if (c != null && cEnd != null) return betweenDates(c, cEnd);
        else if (c != null) return getFormattedDate(c);
        else return null;
    }

    private static String isXSeason(ArrayList<String> words, boolean SQLiteFormat) {
        if (words == null || words.size() == 0) return null;
        String[] of = {"of","from","de","del"};
        String[] last = {"last","pasado","pasada"};
        Calendar c;
        Calendar cEnd = null;
        if (words.stream().anyMatch(w -> w.length() == 2 && w.charAt(0) == 's' && TextUtils.isDigitsOnly(w.substring(1)))) {
            String word = words.stream().filter(w -> w.length() == 2 && w.charAt(0) == 's' && TextUtils.isDigitsOnly(w.substring(1))).findFirst().get();
            int idx = words.indexOf(word);
            int season = Integer.parseInt(word.substring(1));
            c = getSeason(season, true);
            if (c != null) {
                if (SQLiteFormat) cEnd = getSeason(season, false);

                if (idx > 0) {
                    String befWord = words.get(idx - 1);
                    if (equals(befWord, false, last)) {
                        c.add(Calendar.YEAR, -1);
                    }
                }

                if (idx < words.size() - 1) {
                    String nextWord = words.get(idx + 1);
                    if (nextWord.length() == 4 && TextUtils.isDigitsOnly(nextWord)) {
                        try {
                            c.set(Calendar.YEAR, Integer.parseInt(nextWord));
                        } catch (NumberFormatException ignored) {
                        }
                    } else if (nextWord.equals("last_year") || equals(nextWord, false, last)) c.add(Calendar.YEAR, -1);
                    else if (equals(nextWord, false, of) && idx < words.size() - 2) {
                        nextWord = words.get(idx + 2);
                        if (nextWord.length() == 4 && TextUtils.isDigitsOnly(nextWord)) {
                            try {
                                c.set(Calendar.YEAR, Integer.parseInt(nextWord));
                            } catch (NumberFormatException ignored) {
                            }
                        } else if (nextWord.equals("last_year") || equals(nextWord, false, last)) c.add(Calendar.YEAR, -1);
                    }
                }
                if (cEnd != null) cEnd.set(Calendar.YEAR, c.get(Calendar.YEAR));
                if (c != null && cEnd != null) return betweenDates(c, cEnd);
                else if (c != null) return getFormattedDate(c);
            }
        }
        return null;
    }

    private static String isSeason(ArrayList<String> words, boolean SQLiteFormat) {
        if (words == null || words.size() == 0) return null;
        Calendar c = null;
        Calendar cEnd = null;
        if (words.contains("the_season")) {
            c = getSeason( true);
            if (SQLiteFormat) cEnd = getSeason(false);
        } else if (words.contains("last_season")) {
            c = getLastSeason(true);
            if (SQLiteFormat) cEnd = getLastSeason(false);
        }

        if (c != null && cEnd != null) return betweenDates(c, cEnd);
        else if (c != null) return getFormattedDate(c);
        else return null;
    }

    private static Calendar getSeason(int season, boolean first) {
        if (season != 0 && season != 1 && season != 2 && season != 3) return null;
        Calendar c = Calendar.getInstance();

        // Season
        switch (season) {
            case 0: // Spring 22 march
                if (first) {
                    c.set(Calendar.MONTH, Calendar.MARCH);
                    c.set(Calendar.DAY_OF_MONTH, 22);
                } else {
                    c.set(Calendar.MONTH, Calendar.JUNE);
                    c.set(Calendar.DAY_OF_MONTH, 20);
                }
                break;
            case 1: // Summer 21 june
                if (first) {
                    c.set(Calendar.MONTH, Calendar.JUNE);
                } else {
                    c.set(Calendar.MONTH, Calendar.SEPTEMBER);
                }
                c.set(Calendar.DAY_OF_MONTH, 21);
                break;
            case 2: // Fall 22 september
                if (first) {
                    c.set(Calendar.MONTH, Calendar.SEPTEMBER);
                    c.set(Calendar.DAY_OF_MONTH, 22);
                } else {
                    c.set(Calendar.MONTH, Calendar.DECEMBER);
                    c.set(Calendar.DAY_OF_MONTH, 20);
                }
                break;
            case 3: // Winter 21 december
                if (first) {
                    c.set(Calendar.MONTH, Calendar.DECEMBER);
                } else {
                    c.set(Calendar.MONTH, Calendar.MARCH);
                }
                c.set(Calendar.DAY_OF_MONTH, 21);
                break;
        }
        return c;
    }

    private static Calendar getSeason(boolean first) {
        Calendar c = Calendar.getInstance();

        Calendar season = Calendar.getInstance();

        // Spring 22 march - 20 june
        Calendar spring = Calendar.getInstance();
        spring.set(Calendar.MONTH, 2);
        spring.set(Calendar.DAY_OF_MONTH, 22);
        if (c.getTimeInMillis() >= spring.getTimeInMillis()) {
            if (first) {
                season.set(Calendar.MONTH, Calendar.MARCH);
                season.set(Calendar.DAY_OF_MONTH, 22);
            } else {
                season.set(Calendar.MONTH, Calendar.JUNE);
                season.set(Calendar.DAY_OF_MONTH, 20);
            }
        }

        // Summer 21 june - 21 september
        Calendar summer = Calendar.getInstance();
        summer.set(Calendar.MONTH, 5);
        summer.set(Calendar.DAY_OF_MONTH, 21);
        if (c.getTimeInMillis() >= summer.getTimeInMillis()) {
            if (first) {
                season.set(Calendar.MONTH, Calendar.JUNE);
            } else {
                season.set(Calendar.MONTH, Calendar.SEPTEMBER);
            }
            season.set(Calendar.DAY_OF_MONTH, 21);
        }

        // Fall 22 september - 20 december
        Calendar fall = Calendar.getInstance();
        fall.set(Calendar.MONTH, 8);
        fall.set(Calendar.DAY_OF_MONTH, 22);
        if (c.getTimeInMillis() >= fall.getTimeInMillis()) {
            if (first) {
                season.set(Calendar.MONTH, Calendar.SEPTEMBER);
                season.set(Calendar.DAY_OF_MONTH, 22);
            } else {
                season.set(Calendar.MONTH, Calendar.DECEMBER);
                season.set(Calendar.DAY_OF_MONTH, 20);
            }
        }

        // Winter 21 december - 21 march
        Calendar winter = Calendar.getInstance();
        winter.set(Calendar.MONTH, 11);
        winter.set(Calendar.DAY_OF_MONTH, 21);
        if (c.getTimeInMillis() >= winter.getTimeInMillis()) {
            if (first) {
                season.set(Calendar.MONTH, Calendar.DECEMBER);
            } else {
                season.set(Calendar.MONTH, Calendar.MARCH);
            }
            season.set(Calendar.DAY_OF_MONTH, 21);
        }

        return season;
    }

    private static Calendar getLastSeason(boolean first) {
        Calendar c = Calendar.getInstance();

        Calendar season = Calendar.getInstance();

        // Spring 22 march - 20 june -> Now Winter
        Calendar spring = Calendar.getInstance();
        spring.set(Calendar.MONTH, 2);
        spring.set(Calendar.DAY_OF_MONTH, 22);
        if (c.getTimeInMillis() >= spring.getTimeInMillis()) {
            if (first) {
                season.set(Calendar.MONTH, Calendar.DECEMBER);
            } else {
                season.set(Calendar.MONTH, Calendar.MARCH);
            }
            season.set(Calendar.DAY_OF_MONTH, 21);
        }

        // Summer 21 june - 21 september -> Now Spring
        Calendar summer = Calendar.getInstance();
        summer.set(Calendar.MONTH, 5);
        summer.set(Calendar.DAY_OF_MONTH, 21);
        if (c.getTimeInMillis() >= summer.getTimeInMillis()) {
            if (first) {
                season.set(Calendar.MONTH, Calendar.MARCH);
                season.set(Calendar.DAY_OF_MONTH, 22);
            } else {
                season.set(Calendar.MONTH, Calendar.JUNE);
                season.set(Calendar.DAY_OF_MONTH, 20);
            }
        }

        // Fall 22 september - 20 december -> Now Summer
        Calendar fall = Calendar.getInstance();
        fall.set(Calendar.MONTH, 8);
        fall.set(Calendar.DAY_OF_MONTH, 22);
        if (c.getTimeInMillis() >= fall.getTimeInMillis()) {
            if (first) {
                season.set(Calendar.MONTH, Calendar.JUNE);
            } else {
                season.set(Calendar.MONTH, Calendar.SEPTEMBER);
            }
            season.set(Calendar.DAY_OF_MONTH, 21);
        }

        // Winter 21 december - 21 march -> Now Fall
        Calendar winter = Calendar.getInstance();
        winter.set(Calendar.MONTH, 11);
        winter.set(Calendar.DAY_OF_MONTH, 21);
        if (c.getTimeInMillis() >= winter.getTimeInMillis()) {
            if (first) {
                season.set(Calendar.MONTH, Calendar.SEPTEMBER);
                season.set(Calendar.DAY_OF_MONTH, 22);
            } else {
                season.set(Calendar.MONTH, Calendar.DECEMBER);
                season.set(Calendar.DAY_OF_MONTH, 20);
            }
        }

        return season;
    }

    private static String isXYear(ArrayList<String> words, boolean SQLiteFormat) {
        if (words == null || words.size() == 0) return null;
        String[] of = {"of","from","the","el","de","del"};
        Calendar c = null;
        Calendar cEnd = null;
        if (words.stream().anyMatch(w -> w.length() == 4 && TextUtils.isDigitsOnly(w))) {
            String word = words.stream().filter(w -> w.length() == 4 && TextUtils.isDigitsOnly(w)).findFirst().get();
            int year = -1;
            int idx = words.indexOf(word);
            if (idx > 0) {
                String befWord = words.get(idx - 1);
                if (equals(befWord, false, of)) {
                    try {
                        year = Integer.parseInt(word);
                    } catch (NumberFormatException ignored) { }
                    if (year != -1) {
                        c = Calendar.getInstance();
                        c.set(Calendar.YEAR, year);
                        c.set(Calendar.MONTH, 0);
                        c.set(Calendar.DAY_OF_MONTH, 1);
                        if (SQLiteFormat) {
                            cEnd = Calendar.getInstance();
                            cEnd.setTime(c.getTime());
                            cEnd.set(Calendar.MONTH, 11);
                            cEnd.set(Calendar.DATE, cEnd.getActualMaximum(Calendar.DAY_OF_MONTH));
                        }
                    }
                }
            }
            if (c != null && cEnd != null) return betweenDates(c, cEnd);
            else if (c != null) return getFormattedDate(c);
        }
        return null;
    }

    private static String isYear(ArrayList<String> words, boolean SQLiteFormat) {
        if (words == null || words.size() == 0) return null;
        Calendar c = null;
        Calendar cEnd = null;
        if (words.contains("the_year")) {
            c = Calendar.getInstance();
            c.set(Calendar.MONTH, 0);
            c.set(Calendar.DAY_OF_MONTH, 1);
            if (SQLiteFormat) {
                cEnd = Calendar.getInstance();
                cEnd.setTime(c.getTime());
                cEnd.set(Calendar.MONTH, 11);
                cEnd.set(Calendar.DATE, cEnd.getActualMaximum(Calendar.DAY_OF_MONTH));
            }
        } else if (words.contains("last_year")) {
            c = Calendar.getInstance();
            c.add(Calendar.YEAR, -1);
            c.set(Calendar.MONTH, 0);
            c.set(Calendar.DAY_OF_MONTH, 1);
            if (SQLiteFormat) {
                cEnd = Calendar.getInstance();
                cEnd.setTime(c.getTime());
                cEnd.set(Calendar.MONTH, 11);
                cEnd.set(Calendar.DATE, cEnd.getActualMaximum(Calendar.DAY_OF_MONTH));
            }
        }
        if (c != null && cEnd != null) return betweenDates(c, cEnd);
        else if (c != null) return getFormattedDate(c);
        else return null;
    }

    //////////////
    // Date Ago //
    //////////////
    @SuppressLint("SimpleDateFormat")
    private static String getDateAgo(ArrayList<String> words) {
        if (words == null || words.size() == 0) return null;

        for (int i = 0; i < words.size(); i++) {
            String word = words.get(i);
            if (TextUtils.isDigitsOnly(word) && i < words.size() - 1) {
                try {
                    int x = Integer.parseInt(word);
                    String nextWord = words.get(i + 1);
                    Calendar c = null;
                    if (nextWord.length() >= 2 && nextWord.length() <= 3 &&
                            Character.isLetter(nextWord.charAt(0)) &&
                            TextUtils.isDigitsOnly(nextWord.substring(1))) {
                        int number = Integer.parseInt(nextWord.substring(1));
                        switch (nextWord.charAt(0)) {
                            case 'd':
                                return getDayOfWeekAgo(x, number);
                            case 'm':
                                return getMonthOfYearAgo(x, number, true);
                            case 's':
                                return getSeasonOfYearAgo(x, number, true);
                        }
                    } else {
                        switch (nextWord) {
                            case "day":
                                c = Calendar.getInstance();
                                c.add(Calendar.DATE, -x);
                                break;
                            case "week":
                                c = Calendar.getInstance();
                                c.add(Calendar.DATE, -(x * 7));
                                break;
                            case "fortnight":
                                c = Calendar.getInstance();
                                c.add(Calendar.DATE, -(x * 15));
                                break;
                            case "month":
                                c = Calendar.getInstance();
                                c.add(Calendar.MONTH, -x);
                                break;
                            case "season":
                                c = Calendar.getInstance();
                                c.add(Calendar.MONTH, -(x * 3));
                                break;
                            case "year":
                                c = Calendar.getInstance();
                                c.add(Calendar.YEAR, -x);
                                break;
                        }
                    }
                    if (c != null)
                        return new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
                } catch (NumberFormatException ignored) { }
            }
        }
        return null;
    }

    @SuppressLint("SimpleDateFormat")
    private static String getDateAgo(ArrayList<String> words, boolean exactly) {
        if (words == null || words.size() == 0) return null;

        for (int i = 0; i < words.size(); i++) {
            String word = words.get(i);
            if (TextUtils.isDigitsOnly(word) && i < words.size() - 1) {
                try {
                    int x = Integer.parseInt(word);
                    String nextWord = words.get(i + 1);
                    Calendar c = null;
                    Calendar cEnd = null;
                    if (nextWord.length() >= 2 && nextWord.length() <= 3 &&
                            Character.isLetter(nextWord.charAt(0)) &&
                            TextUtils.isDigitsOnly(nextWord.substring(1))) {
                        int number = Integer.parseInt(nextWord.substring(1));
                        switch (nextWord.charAt(0)) {
                            case 'd':
                                return "date(date) = date('" + getDayOfWeekAgo(x, number) + "')";
                            case 'm':
                                return betweenDates(getMonthOfYearAgo(x, number, true), getMonthOfYearAgo(x, number, false));
                            case 's':
                                return betweenDates(getSeasonOfYearAgo(x, number, true), getSeasonOfYearAgo(x, number, false));
                        }
                    } else {
                        switch (nextWord) {
                            case "day":
                                c = Calendar.getInstance();
                                c.add(Calendar.DATE, -x);
                                break;
                            case "week":
                                c = Calendar.getInstance();
                                c.add(Calendar.DATE, -(x * 7));
                                if (!exactly) {
                                    c.set(Calendar.DAY_OF_WEEK, 1);
                                    c.add(Calendar.DAY_OF_WEEK, -6);
                                    cEnd = Calendar.getInstance();
                                    cEnd.setTime(c.getTime());
                                    cEnd.add(Calendar.DATE, 6);
                                }
                                break;
                            case "fortnight":
                                c = Calendar.getInstance();
                                c.add(Calendar.DATE, -(x * 15));
                                if (!exactly) {
                                    cEnd = Calendar.getInstance();
                                    cEnd.setTime(c.getTime());
                                    cEnd.add(Calendar.DATE, 15);
                                }
                                break;
                            case "month":
                                c = Calendar.getInstance();
                                c.add(Calendar.MONTH, -x);
                                if (!exactly) {
                                    c.set(Calendar.DATE, 1);
                                    cEnd = Calendar.getInstance();
                                    cEnd.setTime(c.getTime());
                                    cEnd.set(Calendar.DATE, c.getActualMaximum(Calendar.DAY_OF_MONTH));
                                }
                                break;
                            case "season":
                                c = Calendar.getInstance();
                                c.add(Calendar.MONTH, -(x * 3));
                                if (!exactly) {
                                    cEnd = Calendar.getInstance();
                                    cEnd.setTime(c.getTime());
                                    cEnd.set(Calendar.DATE, c.get(Calendar.DATE));
                                    cEnd.add(Calendar.MONTH, 3);
                                }
                                break;
                            case "year":
                                c = Calendar.getInstance();
                                c.add(Calendar.YEAR, -x);
                                if (!exactly) {
                                    c.set(Calendar.MONTH, 0);
                                    c.set(Calendar.DAY_OF_MONTH, 1);
                                    cEnd = Calendar.getInstance();
                                    cEnd.setTime(c.getTime());
                                    cEnd.set(Calendar.MONTH, 11);
                                    cEnd.set(Calendar.DAY_OF_MONTH, cEnd.getActualMaximum(Calendar.DAY_OF_MONTH));
                                }
                                break;
                        }
                    }
                    if (c != null && cEnd == null)
                        return "date(date) = date('" + new SimpleDateFormat("yyyy-MM-dd").format(c.getTime()) + "')";
                    else if (c != null && cEnd != null)
                        return betweenDates(new SimpleDateFormat("yyyy-MM-dd").format(c.getTime()), new SimpleDateFormat("yyyy-MM-dd").format(cEnd.getTime()));
                } catch (NumberFormatException ignored) { }
            }
        }
        return null;
    }

    // Sunday: 1 | Saturday: 7
    @SuppressLint("SimpleDateFormat")
    private static String getDayOfWeekAgo(int x, int day) {
        Calendar c = Calendar.getInstance();
        int dayOfWeekToday = c.get(Calendar.DAY_OF_WEEK);
        int days = dayOfWeekToday - day;
        days += (x * 7);
        c.add(Calendar.DATE, -Math.abs(days));
        return new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
    }

    private static Calendar getDayOfWeek(int day) {
        if (day < 1 && day > 7) return null;
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, day);
        return c;
    }

    @SuppressLint("SimpleDateFormat")
    private static String getMonthOfYearAgo(int x, int month, boolean first) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.MONTH, month);
        c.add(Calendar.YEAR, -x);
        c.set(Calendar.DAY_OF_MONTH, first ? 1 : c.getActualMaximum(Calendar.DAY_OF_MONTH));
        return new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
    }

    @SuppressLint("SimpleDateFormat")
    private static String getSeasonOfYearAgo(int x, int season, boolean first) {
        // Day of year
        Calendar c = Calendar.getInstance();
        c.add(Calendar.YEAR, -x);

        // Season
        switch (season) {
            case 0: // Spring 22 march
                if (first) {
                    c.set(Calendar.MONTH, Calendar.MARCH);
                    c.set(Calendar.DAY_OF_MONTH, 22);
                } else {
                    c.set(Calendar.MONTH, Calendar.JUNE);
                    c.set(Calendar.DAY_OF_MONTH, 20);
                }
                break;
            case 1: // Summer 21 june
                if (first) {
                    c.set(Calendar.MONTH, Calendar.JUNE);
                } else {
                    c.set(Calendar.MONTH, Calendar.SEPTEMBER);
                }
                c.set(Calendar.DAY_OF_MONTH, 21);
                break;
            case 2: // Fall 22 september
                if (first) {
                    c.set(Calendar.MONTH, Calendar.SEPTEMBER);
                    c.set(Calendar.DAY_OF_MONTH, 22);
                } else {
                    c.set(Calendar.MONTH, Calendar.DECEMBER);
                    c.set(Calendar.DAY_OF_MONTH, 20);
                }
                break;
            case 3: // Winter 21 december
                if (first) {
                    c.set(Calendar.MONTH, Calendar.DECEMBER);
                } else {
                    c.set(Calendar.MONTH, Calendar.MARCH);
                }
                c.set(Calendar.DAY_OF_MONTH, 21);
                break;
        }

        return new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
    }
    ////////////////////////////////////////////////////////////////////////////////


    ///////////////////////
    ///////////////////////
    ///////////////////////
    ///////////////////////
}
