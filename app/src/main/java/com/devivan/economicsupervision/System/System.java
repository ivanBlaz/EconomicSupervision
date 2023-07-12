package com.devivan.economicsupervision.System;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricPrompt;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.airbnb.lottie.LottieAnimationView;
import com.devivan.economicsupervision.Activities.LicensesActivity;
import com.devivan.economicsupervision.Activities.LogIn.LogInStep0_phoneNumber_Activity;
import com.devivan.economicsupervision.Activities.LogIn.LogInStep1_sms_Activity;
import com.devivan.economicsupervision.Activities.LogInOrSignUpActivity;
import com.devivan.economicsupervision.Activities.MovementsActivity;
import com.devivan.economicsupervision.Adapters.FriendsAdapter.FriendsAdapter;
import com.devivan.economicsupervision.Adapters.FriendsOfGroupAdapter.FriendsOfGroupAdapter;
import com.devivan.economicsupervision.Adapters.PaymentsAdapter.PaymentsAdapter;
import com.devivan.economicsupervision.Activities.ConCatActivity;
import com.devivan.economicsupervision.Adapters.VoiceAssistantSentenceAdapter.VoiceAssistantSentencesAdapter;
import com.devivan.economicsupervision.BuildConfig;
import com.devivan.economicsupervision.Fragments.AnimationsFragment;
import com.devivan.economicsupervision.Fragments.ImagesFragment;
import com.devivan.economicsupervision.Fragments.ShouldFragment;
import com.devivan.economicsupervision.Fragments.SoftwareFragment;
import com.devivan.economicsupervision.Objects.Account.Account;
import com.devivan.economicsupervision.Objects.Account.Concepts.Concept;
import com.devivan.economicsupervision.Objects.Account.Friend.Friend;
import com.devivan.economicsupervision.Objects.Account.Group.Group;
import com.devivan.economicsupervision.Objects.Account.Payment.Payment;
import com.devivan.economicsupervision.Objects.Account.Categories.Category;
import com.devivan.economicsupervision.Objects.Account.Categories.SubCategories.SubCategory;
import com.devivan.economicsupervision.Objects.Designer.Designer;
import com.devivan.economicsupervision.Objects.Device.Device;
import com.devivan.economicsupervision.Objects.Device.DeviceAccounts.DeviceAccount;
import com.devivan.economicsupervision.SQLite.SQLiteConnection;
import com.devivan.economicsupervision.Fragments.AddPaymentFragment;
import com.devivan.economicsupervision.Fragments.CategoryFragment;
import com.devivan.economicsupervision.Fragments.ConceptFragment;
import com.devivan.economicsupervision.Fragments.HaveFragment;
import com.devivan.economicsupervision.Fragments.HaveGroupFragment;
import com.devivan.economicsupervision.Activities.MainActivity;
import com.devivan.economicsupervision.R;
import com.devivan.economicsupervision.UtilityClasses.Animations;
import com.devivan.economicsupervision.UtilityClasses.CustomViewPager;
import com.devivan.economicsupervision.UtilityClasses.Designers;
import com.devivan.economicsupervision.UtilityClasses.Images;
import com.devivan.economicsupervision.UtilityClasses.RandomString;
import com.devivan.economicsupervision.Activities.SignUp.SignUpStep0_name_Activity;
import com.devivan.economicsupervision.Activities.SignUp.SignUpStep1_currency_Activity;
import com.devivan.economicsupervision.Activities.SignUp.SignUpStep2_money_Activity;
import com.devivan.economicsupervision.Activities.SignUp.SignUpStep3_phoneNumber_Activity;
import com.devivan.economicsupervision.Activities.SignUp.SignUpStep4_sms_Activity;
import com.devivan.economicsupervision.Activities.TransactionsActivity;
import com.devivan.economicsupervision.UtilityClasses.RelativeTime;
import com.devivan.economicsupervision.UtilityClasses.SizeAdapter;
import com.ebanx.swipebtn.SwipeButton;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.FirebaseStorage;
import com.hbb20.CountryCodePicker;
import com.nightonke.boommenu.Animation.BoomEnum;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;
import com.paypal.android.sdk.fa;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile;
import org.apache.commons.io.FileUtils;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.ghyeok.stickyswitch.widget.StickySwitch;

import static com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.COLLAPSED;
import static com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.DRAGGING;
import static com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.EXPANDED;
import static java.nio.charset.StandardCharsets.UTF_8;


public class System implements Parcelable {

    // Parcelable implementation
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(devId);
        dest.writeString(countryISO);
        dest.writeByte((byte) (countryOn ? 1 : 0));
        dest.writeByte((byte) (auth ? 1 : 0));
        dest.writeByte((byte) (bm ? 1 : 0));
        dest.writeInt(status);
        dest.writeTypedObject(config, flags);
        dest.writeTypedObject(device, flags);
        dest.writeString(def);
        dest.writeByte((byte) (fullScreen ? 1 : 0));
        dest.writeByte((byte) (needBackup ? 1 : 0));
        dest.writeTypedObject(account, flags);
        dest.writeTypedList(ghostAccounts);
        dest.writeDouble(newValue);
        dest.writeDouble(newResult);
        dest.writeInt(calcSymbol);
        dest.writeByte((byte) (comma ? 1 : 0));
        dest.writeString(name);
        dest.writeString(currency);
        dest.writeString(numsBef);
        dest.writeString(numsAft);
        dest.writeDouble(money);
        dest.writeString(phoneNumber);
        dest.writeString(verificationCode);
        dest.writeString(ODId);
    }

    protected System(Parcel in) {
        devId = in.readString();
        countryISO = in.readString();
        countryOn = in.readByte() != 0;
        auth = in.readByte() != 0;
        bm = in.readByte() != 0;
        status = in.readInt();
        config = in.readTypedObject(SystemConfig.CREATOR);
        device = in.readTypedObject(Device.CREATOR);
        def = in.readString();
        fullScreen = in.readByte() != 0;
        needBackup = in.readByte() != 0;
        account = in.readTypedObject(Account.CREATOR);
        ghostAccounts = in.createTypedArrayList(DeviceAccount.CREATOR);
        newValue = in.readDouble();
        newResult = in.readDouble();
        calcSymbol = (char) in.readInt();
        comma = in.readByte() != 0;
        name = in.readString();
        currency = in.readString();
        numsBef = in.readString();
        numsAft = in.readString();
        money = in.readDouble();
        phoneNumber = in.readString();
        verificationCode = in.readString();
        ODId = in.readString();
    }

    public static final Creator<System> CREATOR = new Creator<System>() {
        @Override
        public System createFromParcel(Parcel in) {
            return new System(in);
        }

        @Override
        public System[] newArray(int size) {
            return new System[size];
        }
    };
    //////////////////////////////////////////////////////////////////////////

    // APP DATA //////////////////////////////////////
    //// VERSION /////////////////////////////////////
    /**/ public static final int APP_VERSION = 1; /**/
    //////////////////////////////////////////////////
    //
    //// LIMITS ////////////////////////////////////////////
    /**/ public static final int LIMIT_ACCOUNTS = 3;    /**/
    /**/ public static final int LIMIT_CONCEPTS = 1000; /**/
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**/ public static final int LIMIT_IND_HA = 1000;                                                                   /**/
    /**/ public static final int LIMIT_SH = 1000;                                                                       /**/
    /**/ public static final int LIMIT_GP = 30;                                                                         /**/
    /**/ public static final int LIMIT_MIN_GP_PARTICIPANTS = 2;                                                         /**/
    /**/ public static final int LIMIT_MAX_GP_PARTICIPANTS = 32;                                                        /**/
    /**/ public static final int LIMIT_GP_HA = (LIMIT_GP * LIMIT_MAX_GP_PARTICIPANTS);                                  /**/
    /**/ public static final int LIMIT_HA = LIMIT_IND_HA + LIMIT_GP_HA;                                                 /**/
    /**/ public static final int LIMIT_TRANSACTIONS = 1000000;                                                          /**/
    /**/ public static final int LIMIT_CONCAT = 1000000;                                                                /**/
    /**/ public static final int LIMIT_MOVEMENTS = LIMIT_HA + LIMIT_SH + LIMIT_GP + LIMIT_TRANSACTIONS + LIMIT_CONCAT;  /**/
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**/ public static final double MIN_VALUE = 0.01;                                      /**/
    /**/ public static final double MAX_VALUE = 9999999.99;                                /**/
    /**/ public static final double MAX_MONEY = MAX_VALUE + (MAX_VALUE * LIMIT_MOVEMENTS); /**/
    ///////////////////////////////////////////////////////////////////////////////////////////
    /**/ public static final int DAYS_TO_BACKUP = 7;      /**/
    /**/ public static final int REQUESTS_TO_BACKUP = 35; /**/
    //////////////////////////////////////////////////////////
    /**/ public static final boolean ADMIN = false; /**/
    /////////////////////////////////////////////////////////////////////////////////
    /**/ public static final String TEST_CREDENTIALS_PHONE_NUMBER = "000000000"; /**/
    /////////////////////////////////////////////////////////////////////////////////

    // PayPal
    public static String payPalClientId = "AZ6KsR8HsL5C4Q4iJnRV9AVB1R1HzmJ_H6DZaQRZmNKu8LhNQLr_3QZ7cCW_PzqefocX0eEUibcqy_V0";
    public static final int payPalRequestCode = 12;
    public static final int payPalDonationRequestCode = 13;

    // FATAL ERROR
    public static boolean FATAL_ERROR = false;

    // Going -> Detect if the app is in transit to another activity
    public static boolean going = false;

    // Activity
    public Activity activity;

    // Dialog
    public static AlertDialog dialog;

    // Money format
    public static NumberFormat moneyFormat;

    // Date format
    public static final String datePattern = "yyyy-MM-dd HH:mm:ss";

    // Device data
    String devId;
    public String countryISO;
    /////////////////////////

    // waiting -> boolean for AlertDialogs
    public static boolean waiting = false;

    // Listeners
    public static ListenerRegistration listenerCountry, listenerConfig, listenerDevice;

    // Country config
    boolean countryOn = false;
    boolean auth = false;
    /////////////////////

    // Device config
    boolean bm = false;
    int status = 1;
    ///////////////

    // SystemConfig
    public SystemConfig config;

    // Device
    public static Device device;

    // Config.txt
    public String def = "";
    public static boolean fullScreen = false;
    boolean needBackup = false;
    ///////////////////////////

    // SQLite connection
    SQLiteConnection connection;

    // Accounts
    public static Account account;
    List<DeviceAccount> ghostAccounts;
    //////////////////////////////////

    ////////
    // UI //
    ////////
    // Views
    View lottieView;
    public View infoView;
    public View keyboardView;
    public View keyboardContactsView;
    public View keyboardConceptsView;
    public View microphoneView;
    View shopView;
    View buildAccountView;
    View appInfoView;
    View changeAccountMoneyView;
    View recyclerView;
    View scrollableTextView;

    // Dialogs
    static AlertDialog lottieDialog;
    public AlertDialog infoDialog;
    public AlertDialog keyboardDialog;
    public AlertDialog keyboardContactsDialog;
    public AlertDialog keyboardConceptsDialog;
    public AlertDialog microphoneDialog;
    AlertDialog shopDialog;
    AlertDialog buildAccountDialog;
    AlertDialog appInfoDialog;
    public AlertDialog changeAccountMoneyDialog;
    public AlertDialog recyclerDialog;
    public AlertDialog scrollableTextViewDialog;
    ////////////////////////////////////////////


    public System() {
    }

    @SuppressLint("HardwareIds")
    public System(Activity activity) {
        // Set context
        this.activity = activity;

        // Set money format
        NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.GERMAN);
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);
        DecimalFormatSymbols symbol = new DecimalFormatSymbols(Locale.GERMAN);
        symbol.setDecimalSeparator(',');
        symbol.setGroupingSeparator('.');
        symbol.setCurrencySymbol("");
        String pattern = ((DecimalFormat) nf).toPattern();
        String newPattern = pattern.replaceAll("\\s+", "").trim();
        moneyFormat = new DecimalFormat(newPattern, symbol);
        ////////////////////////////////////////////////////

        // Get id of device
        devId = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);

        // Init TelephonyManager
        TelephonyManager tm = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);

        // Set countryISO using TelephoneManager
        countryISO = tm != null ? tm.getNetworkCountryIso().toUpperCase() : null;
    }

    // Voice assistant utilities
    String[] supportedLanguagesForVoiceAssistant = {"eng", "spa"};

    public static String getSystemISO3Language() {
        return Locale.getDefault().getISO3Language().toLowerCase();
    }

    public boolean doesTheVoiceAssistantSupportTheCurrentLanguage(boolean toast) {
        boolean isCompatible = Arrays.asList(supportedLanguagesForVoiceAssistant).contains(getSystemISO3Language());
        if (!isCompatible && toast) {
            String language = Locale.getDefault().getDisplayLanguage().substring(0, 1).toUpperCase() +
                    Locale.getDefault().getDisplayLanguage().substring(1).toLowerCase();
            toast(activity, WARNING_TOAST, activity.getString(R.string.LANGUAGE_is_not_supported_by_voice_assistant)
                    .replace("LANGUAGE", language), Toast.LENGTH_SHORT);
        }
        return isCompatible;
    }

    /////////////////////////////
    // Data transfer utilities //
    /////////////////////////////
    public void putData(Intent i) {
        i.putExtra("system", this);
    }

    public void putData(Fragment fragment) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("system", this);
        fragment.setArguments(bundle);
    }

    public static System getSystem(Activity activity) {
        return (System) activity.getIntent().getParcelableExtra("system");
    }

    public static System getSystem(Fragment fragment) {
        Bundle bundle = fragment.getArguments();
        return bundle != null ? bundle.getParcelable("system") : null;
    }
    ////////////////////////////////////////////

    // Backup utilities
    private boolean needBackup() {
        boolean need = false;
        if (!config.isBm() && config.isBak()) {
            File db = new File(activity.getFilesDir(), "db.7z");
            if (db.exists()) {
                Date lastModifiedDate = new Date(db.lastModified());
                Date now = Calendar.getInstance().getTime();
                long diff = now.getTime() - lastModifiedDate.getTime();
                int days = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
                int requestsCount = requestsCount();
                if ((days >= DAYS_TO_BACKUP && requestsCount >= REQUESTS_TO_BACKUP / 2) || requestsCount >= REQUESTS_TO_BACKUP)
                    need = true;
            } else need = true;
        }
        return need;
    }

    private void doBackup() {
        __wait__("backup.json");
        compress();
        try {
            FirebaseStorage.getInstance().getReference("SRV/devs/" + devId + "/db.7z").putStream(new FileInputStream(new File(activity.getFilesDir(), "db.7z"))).addOnSuccessListener(taskSnapshot -> {
                // Delete requests
                if (device.getAccounts() != null) {
                    for (int i = 0; i < device.getAccounts().size(); i++) {
                        device.accounts.get(i).setId(device.accounts.get(i).getId().split("#")[0].toLowerCase());
                        device.accounts.get(i).setPos(i);
                        device.getAccounts().get(i).r = new ArrayList<>();
                        device.getAccounts().get(i).auxR = new ArrayList<>();
                    }
                }

                // Clear ghost accounts
                ghostAccounts = new ArrayList<>();

                // Set data in FirebaseDatabase
                FirebaseDatabase.getInstance().getReference("devices/" + devId + "/accounts").setValue(device.getAccounts()).addOnSuccessListener(aVoid12 -> {
                    buildAccountIds();
                    MainActivity a = (MainActivity) activity;
                    a.setContentView(R.layout.activity_main);
                    prepareMainActivity(a);
                    __stopWaiting__();
                });
            });
        } catch (FileNotFoundException ignored) {
        }
    }
    /////////////////////////////

    // Date utilities
    public static String getLocalDateTimeNow() {
        return LocalDateTime.of(LocalDate.now(), LocalTime.now()).format(DateTimeFormatter.ofPattern(System.datePattern));
    }

    // Economy utilities
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public ArrayList<String> getAllCurrencies() {
        ArrayList<String> al = new ArrayList<>();
        Set<Currency> toret = new HashSet<>();
        Locale[] locs = Locale.getAvailableLocales();

        for (Locale loc : locs) {
            try {
                Currency currency = Currency.getInstance(loc);

                if (currency != null) {
                    toret.add(currency);
                    al.add(String.valueOf(currency));
                }
            } catch (Exception exc) {
                // Locale not found
            }
        }
        HashSet<String> hashSet = new HashSet<>();
        hashSet.addAll(al);
        al.clear();
        al.addAll(hashSet);
        return al;
    }

    public boolean doesThisCurrencyExist(String currency) {
        return getAllCurrencies().contains(currency);
    }
    ///////////////////////////////////////////////////////////

    // ID utilities
    private String getName(DeviceAccount deviceAccount) {
        return deviceAccount.getId().split("#")[0].toLowerCase();
    }

    private String getNameFormatted(Account account) {
        String name = account.getId().split("#")[0];
        name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
        return name;
    }

    private String getNameFormatted(DeviceAccount deviceAccount) {
        String name = deviceAccount.getId().split("#")[0];
        name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
        return name;
    }

    public void brokeAccountIds() {
        if (isNotNull(device) && isNotNull(device.getAccounts()) && device.getAccounts().size() > 0) {
            for (DeviceAccount a : device.getAccounts()) {
                a.setId(a.getId().split("#")[0].toLowerCase());
            }
        }
    }

    public void buildAccountIds() {
        if (isNotNull(device) && isNotNull(device.getAccounts()) && device.getAccounts().size() > 0) {
            for (DeviceAccount a : device.getAccounts()) {
                a.setId(a.getId().toLowerCase() + "#" + device.getCountry() + "_" + device.getPhone());
            }
        }
    }
    ////////////////////////////////////

    /////////////////////////
    // UI design utilities //
    /////////////////////////
    // Initialize dialog
    public AlertDialog buildDialog(AlertDialog dialog, View view, int animation, int style, boolean cancelable) {
        if (dialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity, style);
            builder.setView(view);
            dialog = builder.create();
        }

        if (!fullScreen) {
            dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            dialog.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }

        // Set cancelable
        dialog.setCancelable(cancelable);

        // Change status + navigation bars color [ dialog ]
        dialog.getWindow().setStatusBarColor(ContextCompat.getColor(activity, android.R.color.transparent));
        dialog.getWindow().setNavigationBarColor(ContextCompat.getColor(activity, android.R.color.black));

        // Animation
        dialog.getWindow().getAttributes().windowAnimations = animation;

        if (fullScreen) {
            // Flags
            dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

            // System UI Visibility
            dialog.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }

        return dialog;
    }

    public void showDialog(AlertDialog dialog) {
        System.dialog = dialog;
        dialog.show();
        if (fullScreen)
            dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }

    public void enableUI(RecyclerView.OnItemTouchListener disabler, RecyclerView recyclerView, BottomNavigationView bottomNavigationView, CustomViewPager viewPager, TabLayout tabLayout) {
        // Enable recyclerView scroll
        if (recyclerView != null) recyclerView.removeOnItemTouchListener(disabler);

        // Enable bottom navigation view
        if (bottomNavigationView != null) enableBottomNavView(bottomNavigationView, true);

        // Enable pagers pagination
        if (viewPager != null && tabLayout != null)
            enablePagersPagination(viewPager, tabLayout, true);
    }

    public void disableUI(RecyclerView.OnItemTouchListener disabler, RecyclerView recyclerView, BottomNavigationView bottomNavigationView, CustomViewPager viewPager, TabLayout tabLayout) {
        // Disable recyclerView scroll
        if (recyclerView != null) recyclerView.addOnItemTouchListener(disabler);

        // Disable bottom navigation view
        if (bottomNavigationView != null) enableBottomNavView(bottomNavigationView, false);

        // Disable pagers pagination
        if (viewPager != null && tabLayout != null)
            enablePagersPagination(viewPager, tabLayout, false);
    }

    public ArrayList<Object> getCalculatorButtons() {
        MainActivity a = (MainActivity) activity;
        ArrayList<Object> calculatorButtons = new ArrayList<>();

        a.btn0 = a.findViewById(R.id.btn0);
        calculatorButtons.add(a.btn0);

        a.btn1 = a.findViewById(R.id.btn1);
        calculatorButtons.add(a.btn1);

        a.btn2 = a.findViewById(R.id.btn2);
        calculatorButtons.add(a.btn2);

        a.btn3 = a.findViewById(R.id.btn3);
        calculatorButtons.add(a.btn3);

        a.btn4 = a.findViewById(R.id.btn4);
        calculatorButtons.add(a.btn4);

        a.btn5 = a.findViewById(R.id.btn5);
        calculatorButtons.add(a.btn5);

        a.btn6 = a.findViewById(R.id.btn6);
        calculatorButtons.add(a.btn6);

        a.btn7 = a.findViewById(R.id.btn7);
        calculatorButtons.add(a.btn7);

        a.btn8 = a.findViewById(R.id.btn8);
        calculatorButtons.add(a.btn8);

        a.btn9 = a.findViewById(R.id.btn9);
        calculatorButtons.add(a.btn9);

        a.btnMinus = a.findViewById(R.id.btnMinus);
        calculatorButtons.add(a.btnMinus);

        a.btnPlus = a.findViewById(R.id.btnPlus);
        calculatorButtons.add(a.btnPlus);

        a.btnPoint = a.findViewById(R.id.btnPoint);
        calculatorButtons.add(a.btnPoint);

        a.btnBack = a.findViewById(R.id.btnBack);
        calculatorButtons.add(a.btnBack);

        a.lottieArrowTop = a.findViewById(R.id.lottieArrowTop);
        calculatorButtons.add(a.lottieArrowTop);

        a.lottieArrowBottom = a.findViewById(R.id.lottieArrowBottom);
        calculatorButtons.add(a.lottieArrowBottom);

        a.lottieArrowLeft = a.findViewById(R.id.lottieArrowLeft);
        calculatorButtons.add(a.lottieArrowLeft);

        a.lottieArrowRight = a.findViewById(R.id.lottieArrowRight);
        calculatorButtons.add(a.lottieArrowRight);

        a.txtvCalc = a.findViewById(R.id.txtCalc);
        calculatorButtons.add(a.txtvCalc);

        a.txtvResult = a.findViewById(R.id.txtResult);
        calculatorButtons.add(a.txtvResult);

        a.divider = a.findViewById(R.id.divider);
        calculatorButtons.add(a.divider);

        return calculatorButtons;
    }

    @SuppressLint("ClickableViewAccessibility")
    public void enableUI() {
        for (Object o : getCalculatorButtons()) {
            if (o instanceof LottieAnimationView) {
                LottieAnimationView lottie = (LottieAnimationView) o;
                lottie.setClickable(true);
            } else if (o instanceof ImageView) {
                ImageView imageView = (ImageView) o;
                imageView.setClickable(true);
            } else if (o instanceof TextView) {
                TextView textView = (TextView) o;
                textView.setClickable(true);
            } else if (o instanceof View) {
                View view = (View) o;
                view.setClickable(true);
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public void disableUI() {
        for (Object o : getCalculatorButtons()) {
            if (o instanceof LottieAnimationView) {
                LottieAnimationView lottie = (LottieAnimationView) o;
                lottie.setClickable(false);
            } else if (o instanceof ImageView) {
                ImageView imageView = (ImageView) o;
                imageView.setClickable(false);
            } else if (o instanceof TextView) {
                TextView textView = (TextView) o;
                textView.setClickable(false);
            } else if (o instanceof View) {
                View view = (View) o;
                view.setClickable(false);
            }
        }
    }

    public static void showSystemUI(Activity activity) {
        // System UI Visibility
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    public static void hideSystemUI(Activity activity) {
        if (fullScreen) {
            //////////////////////////////
            // Hide system ui of dialog //
            //////////////////////////////
            if (dialog != null) hideDialogUI();
            if (lottieDialog != null) hideDialogUI(lottieDialog);

            ////////////////////////////////
            // Hide system ui of activity //
            ////////////////////////////////
            if (activity != null) hideActivityUI(activity);
        } else {
            showSystemUI(activity);
        }
        // Change status + navigation bars color [ activity ]
        activity.getWindow().setStatusBarColor(ContextCompat.getColor(activity, android.R.color.black));
        activity.getWindow().setNavigationBarColor(ContextCompat.getColor(activity, android.R.color.black));
        if (dialog != null) {
            try {
                // Change status + navigation bars color [ dialog ]
                dialog.getWindow().setStatusBarColor(ContextCompat.getColor(activity, android.R.color.transparent));
                dialog.getWindow().setNavigationBarColor(ContextCompat.getColor(activity, android.R.color.black));
            } catch (Exception ignored) {
            }
        }
    }

    private static void hideDialogUI() {
        // System UI Visibility
        dialog.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    private static void hideDialogUI(AlertDialog dialog) {
        // System UI Visibility
        dialog.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    private static void hideActivityUI(Activity activity) {
        // System UI Visibility
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    public void prepareActivity(Activity activity) {
        this.activity = activity;
        Window window = activity.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(activity, R.color.black));

        initViews();

        //////////////////
        // MainActivity //
        //////////////////
        if (activity instanceof MainActivity) {
            if (account != null) {
                // Check if i have to make a backup
                if (!config.isBm() && !config.isBak()) {
                    if (!needBackup && requestsCount() >= REQUESTS_TO_BACKUP) {
                        FirebaseDatabase.getInstance().getReference("SRV/devs/ex/" + devId).setValue("").addOnSuccessListener(aVoid -> {
                            needBackup = true;
                            rewriteConfig();
                            MainActivity a = (MainActivity) activity;
                            a.setContentView(R.layout.activity_main);
                            prepareMainActivity(a);
                        });
                    } else if (needBackup && requestsCount() < REQUESTS_TO_BACKUP) {
                        needBackup = false;
                        rewriteConfig();
                    } else {
                        MainActivity a = (MainActivity) activity;
                        a.setContentView(R.layout.activity_main);
                        prepareMainActivity(a);
                    }
                } else if (needBackup()) doBackup();
                else {
                    MainActivity a = (MainActivity) activity;
                    a.setContentView(R.layout.activity_main);
                    prepareMainActivity(a);
                }
            }
        }
        ///////////////////////////
        // LogInOrSignUpActivity //
        ///////////////////////////
        else if (activity instanceof LogInOrSignUpActivity) {
            LogInOrSignUpActivity a = (LogInOrSignUpActivity) activity;
            a.lottieAuthentication = a.findViewById(R.id.lottieAuthentication);
            a.txtvAuthentication = a.findViewById(R.id.txtvAuthentication);
            a.txtvLogIn = a.findViewById(R.id.txtvLogIn);
            a.btnLogInO = a.findViewById(R.id.btnLogInO);
            a.lottieLogIn = a.findViewById(R.id.lottieLogIn);
            a.dividerLogIn = a.findViewById(R.id.dividerLogIn);
            a.txtvInfoLogIn = a.findViewById(R.id.txtvInfoLogIn);
            a.dividerSeparator = a.findViewById(R.id.dividerSeparator);
            a.txtvSignUp = a.findViewById(R.id.txtvSignUp);
            a.btnSignUpO = a.findViewById(R.id.btnSignUpO);
            a.lottieSignUp = a.findViewById(R.id.lottieSignUp);
            a.dividerSignUp = a.findViewById(R.id.dividerSignUp);
            a.dividerSignUp1 = a.findViewById(R.id.dividerSignUp1);
            a.txtvInfoSignUp = a.findViewById(R.id.txtvInfoSignUp);

            ////////////////
            // Animations //
            ////////////////
            // From top to bottom
            Animation topToBottom = AnimationUtils.loadAnimation(a, R.anim.slide_in_top);
            a.lottieAuthentication.startAnimation(topToBottom);
            a.txtvAuthentication.startAnimation(topToBottom);

            // From left to right
            Animation leftToRight = AnimationUtils.loadAnimation(a, R.anim.slide_in_right);
            a.txtvLogIn.startAnimation(leftToRight);
            a.btnLogInO.startAnimation(leftToRight);
            a.lottieLogIn.startAnimation(leftToRight);
            a.dividerLogIn.startAnimation(leftToRight);
            a.txtvInfoLogIn.startAnimation(leftToRight);

            // Zoom in
            Animation zoomIn = AnimationUtils.loadAnimation(a, R.anim.zoom_in);
            a.dividerSeparator.startAnimation(zoomIn);

            // From right to left
            Animation slideInLeft = AnimationUtils.loadAnimation(a, R.anim.slide_in_left);
            a.txtvSignUp.startAnimation(slideInLeft);
            a.btnSignUpO.startAnimation(slideInLeft);
            a.lottieSignUp.startAnimation(slideInLeft);
            a.dividerSignUp.startAnimation(slideInLeft);
            a.dividerSignUp1.startAnimation(slideInLeft);
            a.txtvInfoSignUp.startAnimation(slideInLeft);

            //////////////////
            // Click events //
            //////////////////
            // Log In //
            Intent iLogIn = new Intent(a, LogInStep0_phoneNumber_Activity.class);
            iLogIn.putExtra("system", System.this);

            // txtvLogIn
            a.txtvLogIn.setOnClickListener(v -> {
                a.startActivity(iLogIn);
                a.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            });

            // btnLogInO
            a.btnLogInO.setOnClickListener(v -> {
                a.startActivity(iLogIn);
                a.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            });

            // lottieLogIn
            a.lottieLogIn.setOnClickListener(v -> {
                a.startActivity(iLogIn);
                a.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            });

            // Sign Up //
            Intent iSignUp = new Intent(a, SignUpStep0_name_Activity.class);
            iSignUp.putExtra("system", System.this);

            // txtvSignUp
            a.txtvSignUp.setOnClickListener(v -> {
                a.startActivity(iSignUp);
                a.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            });

            // btnSignUpO
            a.btnSignUpO.setOnClickListener(v -> {
                a.startActivity(iSignUp);
                a.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            });

            // lottieSignUp
            a.lottieSignUp.setOnClickListener(v -> {
                a.startActivity(iSignUp);
                a.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            });

        }
        ///////////////////////////////
        // SignUpStep0_name_Activity //
        ///////////////////////////////
        else if (activity instanceof SignUpStep0_name_Activity) {
            SignUpStep0_name_Activity a = (SignUpStep0_name_Activity) activity;
            a.clActivitySignUpStep0Name = a.findViewById(R.id.clActivitySignUpStep0Name);
            a.txtvName = a.findViewById(R.id.txtvName);
            a.txtvName.setText(name);
            a.lottieBack = a.findViewById(R.id.lottieBack);
            a.lottieNext = a.findViewById(R.id.lottieNext);

            // On touch listener
            a.clActivitySignUpStep0Name.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x1 = event.getX();
                        y1 = event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        x2 = event.getX();
                        y2 = event.getY();
                        float deltaX = x2 - x1;
                        if (Math.abs(deltaX) > MIN_DISTANCE) {
                            if (x2 > x1) {
                                a.onBackPressed();
                            } else {
                                a.lottieNext.performClick();
                            }
                        }
                        break;
                }
                return false;
            });

            // txtvName
            a.txtvName.setOnClickListener(v -> customKeyBoard(1, keyboardView.findViewById(R.id.textView), false, false, 10,
                    a.txtvName,
                    R.drawable.box_with_round_selected,
                    R.drawable.box_with_round,
                    () -> {
                        a.onDismiss();
                        return null;
                    }));

            // lottieBack
            a.lottieBack.setOnClickListener(v -> a.onBackPressed());

            // lottieNext
            a.lottieNext.setOnClickListener(v -> {
                a.onDismiss();
                if (len(name) > 0 && len(name) <= 10) {
                    Intent i = new Intent(a, SignUpStep1_currency_Activity.class);
                    i.putExtra("system", this);
                    a.startActivity(i);
                    a.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                } else {
                    toast(a, WARNING_TOAST, a.getString(R.string.The_name_must_be_between_1_and_10_letters), Toast.LENGTH_SHORT);
                }
            });
        }
        ///////////////////////////////////
        // SignUpStep1_currency_Activity //
        ///////////////////////////////////
        else if (activity instanceof SignUpStep1_currency_Activity) {
            SignUpStep1_currency_Activity a = (SignUpStep1_currency_Activity) activity;
            a.clActivitySignUpStep1Currency = a.findViewById(R.id.clActivitySignUpStep1Currency);
            a.txtvCurrency = a.findViewById(R.id.txtvCurrency);
            a.txtvCurrency.setText(currency);
            a.lottieBack = a.findViewById(R.id.lottieBack);
            a.lottieNext = a.findViewById(R.id.lottieNext);

            // On touch listener
            a.clActivitySignUpStep1Currency.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x1 = event.getX();
                        y1 = event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        x2 = event.getX();
                        y2 = event.getY();
                        float deltaX = x2 - x1;
                        if (Math.abs(deltaX) > MIN_DISTANCE) {
                            if (x2 > x1) {
                                a.onBackPressed();
                            } else {
                                a.lottieNext.performClick();
                            }
                        }
                        break;
                }
                return true;
            });

            // txtvCurrency
            a.txtvCurrency.setOnClickListener(v -> customKeyBoard(1, keyboardView.findViewById(R.id.textView), false, false, 3,
                    a.txtvCurrency,
                    R.drawable.box_with_round_selected,
                    R.drawable.box_with_round,
                    () -> {
                        a.onDismiss();
                        return null;
                    }));

            // lottieBack
            a.lottieBack.setOnClickListener(v -> a.onBackPressed());

            // lottieNext
            a.lottieNext.setOnClickListener(v -> {
                a.onDismiss();
                if (len(currency) == 3 && doesThisCurrencyExist(currency)) {
                    Intent i = new Intent(a, SignUpStep2_money_Activity.class);
                    i.putExtra("system", this);
                    a.startActivity(i);
                    a.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                } else {
                    toast(a, 1, "\"" + currency + "\" " + a.getString(R.string.does_not_exist_as_currency), Toast.LENGTH_SHORT);
                }
            });
        }
        ////////////////////////////////
        // SignUpStep2_money_Activity //
        ////////////////////////////////
        else if (activity instanceof SignUpStep2_money_Activity) {
            SignUpStep2_money_Activity a = (SignUpStep2_money_Activity) activity;
            a.clActivitySignUpStep2Money = a.findViewById(R.id.clActivitySignUpStep2Money);
            a.txtvNumsBef = a.findViewById(R.id.txtvNumsBef);
            a.txtvNumsBef.setText(numsBef);
            a.txtvNumsAft = a.findViewById(R.id.txtvNumsAft);
            a.txtvNumsAft.setText(numsAft);
            a.lottieBack = a.findViewById(R.id.lottieBack);
            a.lottieNext = a.findViewById(R.id.lottieNext);

            // On touch listener
            a.clActivitySignUpStep2Money.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x1 = event.getX();
                        y1 = event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        x2 = event.getX();
                        y2 = event.getY();
                        float deltaX = x2 - x1;
                        if (Math.abs(deltaX) > MIN_DISTANCE) {
                            if (x2 > x1) {
                                a.onBackPressed();
                            } else {
                                a.lottieNext.performClick();
                            }
                        }
                        break;
                }
                return true;
            });

            // txtvNumsBef
            a.txtvNumsBef.setOnClickListener(v -> customKeyBoard(0, keyboardView.findViewById(R.id.textView), true, false, 7,
                    a.txtvNumsBef,
                    R.drawable.box_with_round_selected,
                    R.drawable.box_with_round,
                    () -> {
                        a.onDismiss();
                        return null;
                    }));

            // txtvNumsAft
            a.txtvNumsAft.setOnClickListener(v -> customKeyBoard(0, keyboardView.findViewById(R.id.textView), false, false, 2,
                    a.txtvNumsAft,
                    R.drawable.box_with_round_selected,
                    R.drawable.box_with_round,
                    () -> {
                        a.onDismiss();
                        return null;
                    }));

            // lottieBack
            a.lottieBack.setOnClickListener(v -> a.onBackPressed());

            // lottieNext
            a.lottieNext.setOnClickListener(v -> {
                a.onDismiss();
                Intent i = new Intent(a, SignUpStep3_phoneNumber_Activity.class);
                i.putExtra("system", this);
                a.startActivity(i);
                a.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            });
        }
        //////////////////////////////////////
        // SignUpStep3_phoneNumber_Activity //
        //////////////////////////////////////
        else if (activity instanceof SignUpStep3_phoneNumber_Activity) {
            SignUpStep3_phoneNumber_Activity a = (SignUpStep3_phoneNumber_Activity) activity;
            a.clActivitySignUpStep3PhoneNumber = a.findViewById(R.id.clActivitySignUpStep3PhoneNumber);
            a.txtvPhoneNumber = a.findViewById(R.id.txtvPhoneNumber);
            a.txtvPhoneNumber.setText(phoneNumber);
            a.countryCodePicker = a.findViewById(R.id.countryCodePicker);
            a.countryCodePicker.setCountryForNameCode(countryISO);
            a.cbAutoSendSMS = a.findViewById(R.id.cbAutoSendSMS);
            a.btnLicenseTerms = a.findViewById(R.id.btnLicenseTerms);
            a.btnPrivacyPolicy = a.findViewById(R.id.btnPrivacyPolicy);
            a.lottieBack = a.findViewById(R.id.lottieBack);
            a.lottieNext = a.findViewById(R.id.lottieNext);

            // On touch listener
            a.clActivitySignUpStep3PhoneNumber.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x1 = event.getX();
                        y1 = event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        x2 = event.getX();
                        y2 = event.getY();
                        float deltaX = x2 - x1;
                        if (Math.abs(deltaX) > MIN_DISTANCE) {
                            if (x2 > x1) {
                                a.onBackPressed();
                            } else {
                                a.lottieNext.performClick();
                            }
                        }
                        break;
                }
                return true;
            });

            // countryCodePicker
            a.countryCodePicker.setOnCountryChangeListener(a::onDismiss);

            // txtvPhoneNumber
            a.txtvPhoneNumber.setOnClickListener(v -> customKeyBoard(0, keyboardView.findViewById(R.id.textView), true, true, 10,
                    a.txtvPhoneNumber,
                    R.drawable.box_with_round_selected,
                    R.drawable.box_with_round,
                    () -> {
                        a.onDismiss();
                        return null;
                    }));

            // btnLicenseTerms
            a.btnLicenseTerms.setOnClickListener(v -> showLicenses());

            // btnPrivacyPolicy
            a.btnPrivacyPolicy.setOnClickListener(v -> showPrivacyPolicy());

            // lottieBack
            a.lottieBack.setOnClickListener(v -> a.onBackPressed());

            // lottieNext
            a.lottieNext.setOnClickListener(v -> {
                a.onDismiss();
                int len = len(phoneNumber);
                if (len >= 8 && len <= 10) {
                    if (a.cbAutoSendSMS.isChecked()) {
                        tryToConnectToTheInternet(() -> {
                            FirebaseDatabase.getInstance().getReference("users/" + countryISO + "_" + phoneNumber).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (isDataReady(snapshot) || TEST_CREDENTIALS_PHONE_NUMBER.equals(phoneNumber)) {
                                        toast(activity, WARNING_TOAST, activity.getString(R.string.This_phone_number_is_already_registered), Toast.LENGTH_SHORT);
                                    } else {
                                        Intent i = new Intent(a, SignUpStep4_sms_Activity.class);
                                        i.putExtra("system", System.this);
                                        a.startActivity(i);
                                        a.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    toast(activity, WARNING_TOAST, activity.getString(R.string.Something_went_wrong), Toast.LENGTH_SHORT);
                                }
                            });
                            return null;
                        });
                    } else {
                        toast(a, WARNING_TOAST, a.getString(R.string.You_must_accept_the_condition), Toast.LENGTH_SHORT);
                    }
                } else {
                    toast(a, WARNING_TOAST, a.getString(R.string.The_phone_number_must_be_between_8_and_10_digits_long), Toast.LENGTH_SHORT);
                }
            });
        }
        //////////////////////////////
        // SignUpStep4_sms_Activity //
        //////////////////////////////
        else if (activity instanceof SignUpStep4_sms_Activity) {
            SignUpStep4_sms_Activity a = (SignUpStep4_sms_Activity) activity;
            a.clActivitySignUpStep4Sms = a.findViewById(R.id.clActivitySignUpStep4Sms);
            a.txtvWaitingToSMS = a.findViewById(R.id.txtvWaitingToSMS);
            a.txtvVerificationCode = a.findViewById(R.id.txtvVerificationCode);
            a.txtvResendVerificationCode = a.findViewById(R.id.txtvResendVerificationCode);
            a.txtvResendVerificationCode.setEnabled(false);
            a.lottieLoading = a.findViewById(R.id.lottieLoading);
            a.lottieSms = a.findViewById(R.id.lottieSms);
            a.lottieNext = a.findViewById(R.id.lottieNext);

            // System authentication
            SystemAuth systemAuth = new SystemAuth(SignUpStep3_phoneNumber_Activity.originatingAddress, a,
                    () -> {
                            ////////////////////////
                            // Fade out animation //
                            ////////////////////////
                            Animation fade_out = AnimationUtils.loadAnimation(a, R.anim.fade_out);
                            a.txtvResendVerificationCode.setEnabled(false);
                            a.txtvVerificationCode.setEnabled(false);
                            if (a.txtvResendVerificationCode.getVisibility() == View.VISIBLE) a.txtvResendVerificationCode.startAnimation(fade_out);
                            a.lottieSms.startAnimation(fade_out);
                            a.lottieNext.startAnimation(fade_out);
                            //////////////////////////////////////

                            // Animation listener
                            fade_out.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    ///////////////////////
                                    // Change visibility //
                                    ///////////////////////
                                    a.txtvWaitingToSMS.setText(a.getString(R.string.Processing_data));
                                    a.txtvResendVerificationCode.setVisibility(View.INVISIBLE);
                                    a.lottieNext.setVisibility(View.GONE);
                                    //////////////////////////////////////

                                    ///////////////////////
                                    // Fade in animation //
                                    ///////////////////////
                                    Animation fade_in = AnimationUtils.loadAnimation(a, R.anim.fade_in);
                                    a.txtvWaitingToSMS.startAnimation(fade_in);
                                    a.lottieSms.startAnimation(fade_in);
                                    a.lottieSms.setAnimation("data_processing.json");
                                    a.lottieSms.setSpeed(2.5f);
                                    a.lottieSms.playAnimation();
                                    a.lottieLoading.startAnimation(fade_in);
                                    a.lottieLoading.setVisibility(View.VISIBLE);
                                    ////////////////////////////////////////////

                                    // Animation listener
                                    fade_in.setAnimationListener(new Animation.AnimationListener() {
                                        @Override
                                        public void onAnimationStart(Animation animation) {

                                        }

                                        @Override
                                        public void onAnimationEnd(Animation animation) {
                                            // Start registration process in the system
                                            smsReceived();
                                        }

                                        @Override
                                        public void onAnimationRepeat(Animation animation) {

                                        }
                                    });
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {

                                }
                            });
                            return null; },
                    () -> { toast(a, WARNING_TOAST, a.getString(R.string.Wrong_verification_code), Toast.LENGTH_SHORT); return null; });

            // System authentication callbacks
            PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                    try { systemAuth.signInWithPhoneAuthCredential(phoneAuthCredential); } catch (Exception ignored) { }
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    toast(a, INFO_TOAST, a.getString(R.string.Verification_error), Toast.LENGTH_SHORT);
                }

                @Override
                public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent(verificationId, forceResendingToken);
                    systemAuth.verificationId = verificationId;
                    systemAuth.forceResendingToken = forceResendingToken;

                    toast(a, INFO_TOAST, a.getString(R.string.Enter_the_verification_code_we_have_sent_you), Toast.LENGTH_SHORT);
                }
            };
            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

            // On touch listener
            a.clActivitySignUpStep4Sms.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x1 = event.getX();
                        y1 = event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        x2 = event.getX();
                        y2 = event.getY();
                        float deltaX = x2 - x1;
                        if (Math.abs(deltaX) > MIN_DISTANCE) {
                            if (x2 > x1) {
                                a.onBackPressed();
                            } else {
                                a.lottieNext.performClick();
                            }
                        }
                        break;
                }
                return true;
            });

            // txtvVerificationCode
            a.txtvVerificationCode.setOnClickListener(v -> customKeyBoard(0, keyboardView.findViewById(R.id.textView), true, true, 11,
                    a.txtvVerificationCode,
                    R.drawable.box_with_round_selected,
                    R.drawable.box_with_round,
                    () -> {
                        a.onDismiss();
                        return null;
                    }));

            // txtvResendVerificationCode
            a.txtvResendVerificationCode.setOnClickListener(v -> {
                a.txtvResendVerificationCode.setOnClickListener(null);
                Animation fadeOut = AnimationUtils.loadAnimation(a, R.anim.fade_out);
                a.txtvResendVerificationCode.startAnimation(fadeOut);
                fadeOut.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        a.txtvResendVerificationCode.setVisibility(View.INVISIBLE);
                        try { systemAuth.resendVerificationCode(callbacks); } catch (Exception ignored) { }
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            });

            // lottieNext
            a.lottieNext.setOnClickListener(v -> {
                if (isNotNull(systemAuth.verificationId) && isNotNull(verificationCode)) {
                    try { systemAuth.verifyPhoneNumberWithVerificationCode(verificationCode); } catch (Exception ignored) { }
                }
            });

            // Send sms verification code
            try {
                systemAuth.sendVerificationCode(callbacks);

                // Timeout to enable verification code resend
                new CountDownTimer(15000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        /////////////////
                        // Update time //
                        /////////////////
                        String timeLeftText = "" + ((int) millisUntilFinished % 60000 / 1000);
                        a.txtvResendVerificationCode.setText(a.getString((R.string.Resend_verification_code)) + " (" + timeLeftText + " " + a.getString(R.string.seconds) + ")");
                        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    }

                    @Override
                    public void onFinish() {
                        // Cancel CountDownTimer
                        this.cancel();

                        // Enable verification code resend
                        a.txtvResendVerificationCode.setText(a.getString(R.string.Resend_verification_code));
                        a.txtvResendVerificationCode.setEnabled(true);
                        //////////////////////////////////////////////
                    }
                }.start();
            } catch (Exception ignored) { }
        }
        /////////////////////////////////////
        // LogInStep0_phoneNumber_Activity //
        /////////////////////////////////////
        else if (activity instanceof LogInStep0_phoneNumber_Activity) {
            LogInStep0_phoneNumber_Activity a = (LogInStep0_phoneNumber_Activity) activity;
            a.clActivityLogInStep0PhoneNumber = a.findViewById(R.id.clActivityLogInStep0PhoneNumber);
            a.txtvPhoneNumber = a.findViewById(R.id.txtvPhoneNumber);
            a.txtvPhoneNumber.setText(phoneNumber);
            a.countryCodePicker = a.findViewById(R.id.countryCodePicker);
            a.countryCodePicker.setCountryForNameCode(countryISO);
            a.cbAutoSendSMS = a.findViewById(R.id.cbAutoSendSMS);
            a.btnLicenseTerms = a.findViewById(R.id.btnLicenseTerms);
            a.btnPrivacyPolicy = a.findViewById(R.id.btnPrivacyPolicy);
            a.lottieBack = a.findViewById(R.id.lottieBack);
            a.lottieNext = a.findViewById(R.id.lottieNext);

            // On touch listener
            a.clActivityLogInStep0PhoneNumber.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x1 = event.getX();
                        y1 = event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        x2 = event.getX();
                        y2 = event.getY();
                        float deltaX = x2 - x1;
                        if (Math.abs(deltaX) > MIN_DISTANCE) {
                            if (x2 > x1) {
                                a.onBackPressed();
                            } else {
                                a.lottieNext.performClick();
                            }
                        }
                        break;
                }
                return true;
            });

            // countryCodePicker
            a.countryCodePicker.setOnCountryChangeListener(a::onDismiss);

            // txtvPhoneNumber
            a.txtvPhoneNumber.setOnClickListener(v -> customKeyBoard(0, keyboardView.findViewById(R.id.textView), true, true, 10,
                    a.txtvPhoneNumber,
                    R.drawable.box_with_round_selected,
                    R.drawable.box_with_round,
                    () -> {
                        a.onDismiss();
                        return null;
                    }));

            // btnLicenseTerms
            a.btnLicenseTerms.setOnClickListener(v -> showLicenses());

            // btnPrivacyPolicy
            a.btnPrivacyPolicy.setOnClickListener(v -> showPrivacyPolicy());

            // lottieBack
            a.lottieBack.setOnClickListener(v -> a.onBackPressed());

            // lottieNext
            a.lottieNext.setOnClickListener(v -> {
                a.onDismiss();
                int len = len(phoneNumber);
                if (len >= 8 && len <= 10) {
                    if (a.cbAutoSendSMS.isChecked()) {
                        a.lottieNext.setEnabled(false);
                        tryToConnectToTheInternet(() -> {
                            countryISO = TEST_CREDENTIALS_PHONE_NUMBER.equals(phoneNumber) ? "" : countryISO;
                            FirebaseDatabase.getInstance().getReference("users/" + countryISO + "_" + phoneNumber).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (isDataReady(snapshot)) {
                                        // Set ODId
                                        ODId = snapshot.getValue().toString();

                                        // Start activity
                                        Intent i = new Intent(a, LogInStep1_sms_Activity.class);
                                        i.putExtra("system", System.this);
                                        a.startActivity(i);
                                        a.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                                        /////////////////////////////////////////////////////////////////////////
                                    } else {
                                        toast(activity, WARNING_TOAST, activity.getString(R.string.This_phone_number_is_not_registered), Toast.LENGTH_SHORT);
                                        a.lottieNext.setEnabled(true);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    toast(activity, WARNING_TOAST, activity.getString(R.string.Something_went_wrong), Toast.LENGTH_SHORT);
                                    a.lottieNext.setEnabled(true);
                                }
                            });
                            return null;
                        });
                    } else {
                        toast(a, WARNING_TOAST, a.getString(R.string.You_must_accept_the_condition), Toast.LENGTH_SHORT);
                    }
                } else {
                    toast(a, WARNING_TOAST, a.getString(R.string.The_phone_number_must_be_between_8_and_10_digits_long), Toast.LENGTH_SHORT);
                }
            });
        }
        /////////////////////////////
        // LogInStep1_sms_Activity //
        /////////////////////////////
        else if (activity instanceof LogInStep1_sms_Activity) {
            LogInStep1_sms_Activity a = (LogInStep1_sms_Activity) activity;
            a.clActivityLogInStep1Sms = a.findViewById(R.id.clActivityLogInStep1Sms);
            a.txtvWaitingToSMS = a.findViewById(R.id.txtvWaitingToSMS);
            a.txtvVerificationCode = a.findViewById(R.id.txtvVerificationCode);
            a.txtvResendVerificationCode = a.findViewById(R.id.txtvResendVerificationCode);
            a.txtvResendVerificationCode.setEnabled(false);
            a.lottieLoading = a.findViewById(R.id.lottieLoading);
            a.lottieSms = a.findViewById(R.id.lottieSms);
            a.lottieNext = a.findViewById(R.id.lottieNext);

            // System authentication
            SystemAuth systemAuth = new SystemAuth(LogInStep0_phoneNumber_Activity.originatingAddress, a,
                    () -> {
                        ////////////////////////
                        // Fade out animation //
                        ////////////////////////
                        Animation fade_out = AnimationUtils.loadAnimation(a, R.anim.fade_out);
                        a.txtvResendVerificationCode.setEnabled(false);
                        a.txtvVerificationCode.setEnabled(false);
                        a.txtvWaitingToSMS.startAnimation(fade_out);
                        if (a.txtvResendVerificationCode.getVisibility() == View.VISIBLE) a.txtvResendVerificationCode.startAnimation(fade_out);
                        a.lottieSms.startAnimation(fade_out);
                        a.lottieNext.startAnimation(fade_out);
                        //////////////////////////////////////

                        // Animation listener
                        fade_out.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                ///////////////////////
                                // Change visibility //
                                ///////////////////////
                                a.txtvWaitingToSMS.setText(a.getString(R.string.Processing_data));
                                a.txtvResendVerificationCode.setVisibility(View.INVISIBLE);
                                a.lottieNext.setVisibility(View.GONE);
                                //////////////////////////////////////

                                ///////////////////////
                                // Fade in animation //
                                ///////////////////////
                                Animation fade_in = AnimationUtils.loadAnimation(a, R.anim.fade_in);
                                a.txtvWaitingToSMS.startAnimation(fade_in);
                                a.lottieSms.startAnimation(fade_in);
                                a.lottieSms.setAnimation("data_processing.json");
                                a.lottieSms.setSpeed(2.5f);
                                a.lottieSms.playAnimation();
                                a.lottieLoading.startAnimation(fade_in);
                                a.lottieLoading.setVisibility(View.VISIBLE);
                                ////////////////////////////////////////////

                                // Animation listener
                                fade_in.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        /////////////////
                                        // Portability //
                                        /////////////////
                                        startPortability();
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        return null; },
                    () -> { toast(a, WARNING_TOAST, a.getString(R.string.Wrong_verification_code), Toast.LENGTH_SHORT); return null; });

            // System authentication callbacks
            PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                    try { systemAuth.signInWithPhoneAuthCredential(phoneAuthCredential); } catch (Exception ignored) { }
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    toast(a, INFO_TOAST, a.getString(R.string.Verification_error), Toast.LENGTH_SHORT);
                }

                @Override
                public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent(verificationId, forceResendingToken);
                    systemAuth.verificationId = verificationId;
                    systemAuth.forceResendingToken = forceResendingToken;

                    toast(a, INFO_TOAST, a.getString(R.string.Enter_the_verification_code_we_have_sent_you), Toast.LENGTH_SHORT);
                }
            };
            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

            // On touch listener
            a.clActivityLogInStep1Sms.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x1 = event.getX();
                        y1 = event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        x2 = event.getX();
                        y2 = event.getY();
                        float deltaX = x2 - x1;
                        if (Math.abs(deltaX) > MIN_DISTANCE) {
                            if (x2 > x1) {
                                a.onBackPressed();
                            } else {
                                a.lottieNext.performClick();
                            }
                        }
                        break;
                }
                return true;
            });

            // txtvVerificationCode
            a.txtvVerificationCode.setOnClickListener(v -> customKeyBoard(0, keyboardView.findViewById(R.id.textView), true, true, 11,
                    a.txtvVerificationCode,
                    R.drawable.box_with_round_selected,
                    R.drawable.box_with_round,
                    () -> {
                        a.onDismiss();
                        return null;
                    }));

            // txtvResendVerificationCode
            a.txtvResendVerificationCode.setOnClickListener(v -> {
                a.txtvResendVerificationCode.setOnClickListener(null);
                Animation fadeOut = AnimationUtils.loadAnimation(a, R.anim.fade_out);
                a.txtvResendVerificationCode.startAnimation(fadeOut);
                fadeOut.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        a.txtvResendVerificationCode.setVisibility(View.INVISIBLE);
                        try { systemAuth.resendVerificationCode(callbacks); } catch (Exception ignored) { }
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            });

            // lottieNext
            a.lottieNext.setOnClickListener(v -> {
                if (isNotNull(systemAuth.verificationId) && isNotNull(verificationCode)) {
                    try { systemAuth.verifyPhoneNumberWithVerificationCode(verificationCode); } catch (Exception ignored) { }
                }
            });

            // Send sms verification code
            try {
                if (LogInStep0_phoneNumber_Activity.originatingAddress.endsWith(TEST_CREDENTIALS_PHONE_NUMBER)) {
                    startPortability();
                } else {
                    systemAuth.sendVerificationCode(callbacks);
                }

                // Timeout to enable verification code resend
                new CountDownTimer(15000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        /////////////////
                        // Update time //
                        /////////////////
                        String timeLeftText = "" + ((int) millisUntilFinished % 60000 / 1000);
                        a.txtvResendVerificationCode.setText(a.getString((R.string.Resend_verification_code)) + " (" + timeLeftText + " " + a.getString(R.string.seconds) + ")");
                        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    }

                    @Override
                    public void onFinish() {
                        // Cancel CountDownTimer
                        this.cancel();

                        // Enable verification code resend
                        a.txtvResendVerificationCode.setText(a.getString(R.string.Resend_verification_code));
                        a.txtvResendVerificationCode.setEnabled(true);
                        //////////////////////////////////////////////
                    }
                }.start();
            } catch (Exception ignored) { }
        }
        ////////////////    ///////////////
        // ↓ BOTTOM ↓ // -> // Movements //
        ////////////////    ///////////////
        else if (activity instanceof MovementsActivity) {
            MovementsActivity a = (MovementsActivity) activity;
            // Init speech
            a.initSpeech();

            //////////
            // Find //
            //////////
            // txtvFilter
            a.txtvFilter = a.findViewById(R.id.txtvFilter);

            // Display money info [ money balance | profits | losses ]
            a.displayMoney();

            // lottieFilter
            a.lottieFilter = a.findViewById(R.id.lottieFilter);
            a.lottieFilter.setVisibility(View.GONE);
            a.lottieFilter.addAnimatorListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    a.lottieFilter.setVisibility(View.GONE);
                    a.txtvFilter.setText(a.getString(R.string.Filter));
                }
            });

            // lottieVoiceAssistant
            a.lottieVoiceAssistant = a.findViewById(R.id.lottieVoiceAssistant);
            a.lottieVoiceAssistant.setOnClickListener(v -> a.closeSpeech());

            // btnVoiceAssistantHelp
            a.btnVoiceAssistantHelp = a.findViewById(R.id.btnVoiceAssistantHelp);
            a.btnVoiceAssistantHelp.setOnClickListener(v -> showRecyclerViewDialog(true));

            // slidingUpPanelLayout
            a.slidingUpPanelLayout = a.findViewById(R.id.slidingUpMovements);
            a.slidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
                @Override
                public void onPanelSlide(View panel, float slideOffset) {
                    a.txtvFilter.setAlpha(1 - slideOffset);
                    a.lottieVoiceAssistant.setAlpha(slideOffset);
                }

                @Override
                public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                    if (newState == EXPANDED) {
                        if (doesTheVoiceAssistantSupportTheCurrentLanguage(false)) {
                            if (device.isPremium()) {
                                if (!FATAL_ERROR) a.openSpeech();
                                else if (previousState == DRAGGING)
                                    a.slidingUpPanelLayout.setPanelState(COLLAPSED);
                            } else a.slidingUpPanelLayout.setPanelState(COLLAPSED);
                        } else a.slidingUpPanelLayout.setPanelState(COLLAPSED);
                    } else if (previousState == COLLAPSED && newState == DRAGGING) {

                        if (doesTheVoiceAssistantSupportTheCurrentLanguage(true)) {
                            if (!System.device.isPremium()) {
                                toast(a, CONFIRMATION_TOAST, a.getString(R.string.Go_premium_and_you_can_enjoy_this_option), Toast.LENGTH_SHORT);
                                a.slidingUpPanelLayout.setPanelState(COLLAPSED);
                            }
                        }
                    } else if (newState == COLLAPSED) a.closeSpeech();
                }
            });

            // rvMovements
            a.rvMovements = a.findViewById(R.id.rvMovements);
            a.rvMovements.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                        a.isScrolling = true;
                        Animation zoom_out = AnimationUtils.loadAnimation(activity, R.anim.zoom_out);
                        zoom_out.setDuration(100);
                        a.btnVoiceAssistantHelp.startAnimation(zoom_out);
                        zoom_out.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                a.btnVoiceAssistantHelp.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                    } else if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                        Animation zoom_in = AnimationUtils.loadAnimation(activity, R.anim.zoom_in);
                        zoom_in.setDuration(100);
                        a.btnVoiceAssistantHelp.setVisibility(View.VISIBLE);
                        a.btnVoiceAssistantHelp.startAnimation(zoom_in);
                    }
                }

                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    LinearLayoutManager llm = (LinearLayoutManager) recyclerView.getLayoutManager();
                    assert llm != null;
                    a.currentItems = llm.getChildCount();
                    a.totalItems = llm.getItemCount();
                    a.scrollOutItems = llm.findFirstVisibleItemPosition();

                    if (a.isScrolling && (a.currentItems + a.scrollOutItems == a.totalItems) && (VoiceAssistant.isPayment(VoiceAssistant.tGroupBy) || VoiceAssistant.isGroup(VoiceAssistant.tGroupBy))) {
                        if (doesTheDatabaseExist() && !isDatabaseCorrupt()) {
                            a.isScrolling = false;
                            VoiceAssistant.nextData();
                            a.lottieFilter.setVisibility(View.VISIBLE);
                            a.lottieFilter.playAnimation();
                            a.txtvFilter.setText(a.getString(R.string.Filtering));
                        } else {
                            FATAL_ERROR = true;
                            a.onBackPressed();
                        }
                    }
                }
            });

            // Filter data
            if (doesTheDatabaseExist() && !isDatabaseCorrupt()) {
                VoiceAssistant.filterData(this, a.rvMovements, null, null);
            } else {
                FATAL_ERROR = true;
                a.onBackPressed();
            }
        }
        /////////////    //////////////////
        // ↑ TOP ↑ // -> // Transactions //
        /////////////    //////////////////
        else if (activity instanceof TransactionsActivity) {
            TransactionsActivity a = (TransactionsActivity) activity;
            // Find voice assistant controls
            a.clVoiceAssistant = a.findViewById(R.id.clVoiceAssistant);
            a.clVoiceAssistant.setVisibility(View.GONE);
            a.lottieVoiceAssistant = a.findViewById(R.id.lottieVoiceAssistant);
            ///////////////////////////////////////////////////////////////////
            // Prepare bottom navigation view
            a.bottomNavView = a.findViewById(R.id.bottomNavView);
            a.bottomNavView.setOnNavigationItemSelectedListener(a.navListener);
            //
            a.bottomNavView.getMenu().findItem(R.id.nav_should).setTitle(moneyFormat.format(System.account.getShould()));
            a.bottomNavView.getMenu().findItem(R.id.nav_transaction).setTitle(moneyFormat.format(System.account.getTransact()));
            a.bottomNavView.getMenu().findItem(R.id.nav_have).setTitle(moneyFormat.format(System.account.getHave()));
            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            //
            a.bottomNavView.setSelectedItemId(R.id.nav_transaction);
        }
        //////////////    ////////////////    ///////////////    //////////////
        // ← LEFT ← // -> // Categories //    // → RIGHT → // -> // Concepts //
        //////////////    ////////////////    ///////////////    //////////////
        else if (activity instanceof ConCatActivity) {
            ConCatActivity a = (ConCatActivity) activity;
            a.btnGoToConCat = a.findViewById(R.id.btnGoToConCat);
            a.btnGoToConCat.setEnabled(true);
            a.btnGoToConCat.setOnClickListener(v -> {
                v.setEnabled(false);
                if (ConCatActivity.isCon()) ConCatActivity.setCat(true);
                else {
                    if (a.categoryFragment.isVisible() && a.categoryFragment.rvCategories != null) {
                        a.categoryFragment.rvCategories.setEnabled(true);
                        disableUI(new RecyclerViewDisabler(), a.categoryFragment.rvCategories, null, null, null);
                    }
                    ConCatActivity.setCon(true);
                }
                a.onBackPressed();
            });

            ViewPager conCatViewPager = a.findViewById(R.id.conCatViewPager);
            TabLayout conCatTabLayout = a.findViewById(R.id.conCatTabLayout);

            ConCatActivity.ViewPagerAdapter viewPagerAdapter = new ConCatActivity.ViewPagerAdapter(a.getSupportFragmentManager(), 0);

            a.conceptFragment = new ConceptFragment();
            putData(a.conceptFragment);

            a.categoryFragment = new CategoryFragment();
            putData(a.categoryFragment);

            if (ConCatActivity.isCat()) {
                // Categories
                viewPagerAdapter.addFragment(a.categoryFragment, activity.getString(R.string.Categories));
                a.btnGoToConCat.setVisibility(View.VISIBLE);
                a.btnGoToConCat.setText(activity.getString(R.string.Concepts));
            } else if (ConCatActivity.isCon()) {
                // Concepts
                viewPagerAdapter.addFragment(a.conceptFragment, activity.getString(R.string.Concepts));
                a.btnGoToConCat.setVisibility(View.VISIBLE);
                a.btnGoToConCat.setText(activity.getString(R.string.Categories));
            } else {
                // Categories and Concepts -> reader mode
                viewPagerAdapter.addFragment(a.categoryFragment, activity.getString(R.string.Categories));
                viewPagerAdapter.addFragment(a.conceptFragment, activity.getString(R.string.Concepts));
            }

            conCatViewPager.setAdapter(viewPagerAdapter);
            conCatViewPager.setNestedScrollingEnabled(false);
            conCatTabLayout.setupWithViewPager(conCatViewPager);
            conCatViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    viewPagerAdapter.getItem(position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }
        //////////////
        // LICENSES //
        //////////////
        else if (activity instanceof LicensesActivity) {
            LicensesActivity a = (LicensesActivity) activity;

            ViewPager imgAnimSoftViewPager = a.findViewById(R.id.imgAnimSoftViewPager);
            TabLayout imgAnimSoftTabLayout = a.findViewById(R.id.imgAnimSoftTabLayout);

            ConCatActivity.ViewPagerAdapter viewPagerAdapter = new ConCatActivity.ViewPagerAdapter(a.getSupportFragmentManager(), 0);

            a.imagesFragment = new ImagesFragment();
            putData(a.imagesFragment);

            a.animationsFragment = new AnimationsFragment();
            putData(a.animationsFragment);

            a.softwareFragment = new SoftwareFragment();
            putData(a.softwareFragment);

            viewPagerAdapter.addFragment(a.imagesFragment, activity.getString(R.string.Images));
            viewPagerAdapter.addFragment(a.animationsFragment, activity.getString(R.string.Animations));
            viewPagerAdapter.addFragment(a.softwareFragment, activity.getString(R.string.Software));

            imgAnimSoftViewPager.setAdapter(viewPagerAdapter);
            imgAnimSoftViewPager.setNestedScrollingEnabled(false);
            imgAnimSoftTabLayout.setupWithViewPager(imgAnimSoftViewPager);
            imgAnimSoftViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    viewPagerAdapter.getItem(position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        } else
            toast(activity, WARNING_TOAST, activity.getString(R.string.Something_went_wrong), Toast.LENGTH_SHORT);
    }


    ///////////////////////
    // Calculator values //
    ///////////////////////
    public boolean pointEnabled = true;
    static double newValue = 0.00, newResult = 0.00;
    static char calcSymbol = '?';
    public boolean comma = false;
    float x1, x2, y1, y2;
    static int MIN_DISTANCE = 75;
    /////////////////////////////

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    private void prepareMainActivity(MainActivity a) {
        // Not waiting
        waiting = false;

        ///////////////////////
        // Calculator screen //
        ///////////////////////
        // Find
        a.txtvCalc = a.findViewById(R.id.txtCalc);
        a.divider = a.findViewById(R.id.divider);
        a.txtvResult = a.findViewById(R.id.txtResult);
        a.txtvName = a.findViewById(R.id.txtName);

        // Values
        a.txtvCalc.setText(moneyFormat.format(account.getMoney()) + " " + calcSymbol + " " +
                (newValue != 0 ? moneyFormat.format(newValue) : "?"));

        if (calcSymbol != '?')
            newResult = calcSymbol == '+' ? account.getMoney() + newValue : account.getMoney() - newValue;
        a.txtvResult.setText("= " + (newResult != 0.00 ? moneyFormat.format(newResult) : '?'));

        // Animation
        a.divider.setAnimation(AnimationUtils.loadAnimation(a, R.anim.zoom_in));
        a.txtvCalc.setAnimation(AnimationUtils.loadAnimation(a, R.anim.slide_in_right));
        a.txtvResult.setAnimation(AnimationUtils.loadAnimation(a, R.anim.slide_in_left));
        /////////////////////////////////////////////////////////////////////////////////

        // Listener
        a.txtvCalc.setOnClickListener(v -> {
            if (!FATAL_ERROR) changeAccountMoney();
        });
        a.divider.setOnClickListener(v -> {
            if (!FATAL_ERROR) changeAccountMoney();
        });
        a.txtvResult.setOnClickListener(v -> {
            if (!FATAL_ERROR) changeAccountMoney();
        });

        ////////////////////////
        // Calculator numbers //
        ////////////////////////
        // Find
        ArrayList<LottieAnimationView> numbers = new ArrayList<>();

        a.btn0 = a.findViewById(R.id.btn0);
        numbers.add(a.btn0);

        a.btn1 = a.findViewById(R.id.btn1);
        numbers.add(a.btn1);

        a.btn2 = a.findViewById(R.id.btn2);
        numbers.add(a.btn2);

        a.btn3 = a.findViewById(R.id.btn3);
        numbers.add(a.btn3);

        a.btn4 = a.findViewById(R.id.btn4);
        numbers.add(a.btn4);

        a.btn5 = a.findViewById(R.id.btn5);
        numbers.add(a.btn5);

        a.btn6 = a.findViewById(R.id.btn6);
        numbers.add(a.btn6);

        a.btn7 = a.findViewById(R.id.btn7);
        numbers.add(a.btn7);

        a.btn8 = a.findViewById(R.id.btn8);
        numbers.add(a.btn8);

        a.btn9 = a.findViewById(R.id.btn9);
        numbers.add(a.btn9);

        // Events and Animations
        for (LottieAnimationView number : numbers) {
            // Event
            number.setOnClickListener(v -> calculatorNumberKeyClickEvent(a.txtvCalc, a.txtvResult, (LottieAnimationView) v));

            // Animation
            number.setMaxFrame(50);
            number.setSpeed(2.5f);
            number.playAnimation();
            ///////////////////////
        }
        ////////////////////////////////////////////////

        /////////////////////
        // Calculator back //
        /////////////////////
        // Find
        a.btnBack = a.findViewById(R.id.btnBack);

        // Event
        a.btnBack.setOnClickListener(v -> calculatorBackKeyClickEvent(a.txtvCalc, a.txtvResult, (LottieAnimationView) v, false));
        a.btnBack.setOnLongClickListener(v -> {
            calculatorBackKeyClickEvent(a.txtvCalc, a.txtvResult, (LottieAnimationView) v, true);
            return true;
        });

        // Animation
        a.btnBack.setMinFrame(20);
        a.btnBack.setMaxFrame(70);
        a.btnBack.setSpeed(2.5f);
        a.btnBack.playAnimation();

        //////////////////////
        // Calculator + / - //
        //////////////////////
        // Find
        a.btnPlus = a.findViewById(R.id.btnPlus);
        a.btnMinus = a.findViewById(R.id.btnMinus);

        // Event
        a.btnPlus.setOnClickListener(v -> calculatorCalcSymbolKeyClickEvent(a.txtvCalc, a.txtvResult, (ImageView) v));
        a.btnMinus.setOnClickListener(v -> calculatorCalcSymbolKeyClickEvent(a.txtvCalc, a.txtvResult, (ImageView) v));

        // Animation
        a.btnPlus.setAnimation(AnimationUtils.loadAnimation(a, R.anim.slide_in_left));
        a.btnMinus.setAnimation(AnimationUtils.loadAnimation(a, R.anim.slide_in_right));

        //////////////////////
        // Calculator point //
        //////////////////////
        // Find
        a.btnPoint = a.findViewById(R.id.btnPoint);
        a.btnPoint.setAnimation(AnimationUtils.loadAnimation(a, R.anim.zoom_in));

        ///////////
        // Event //
        ///////////

        // Top arrow click listener -> TransactionsActivity
        a.lottieArrowTop = a.findViewById(R.id.lottieArrowTop);
        a.lottieArrowTop.setOnClickListener(v -> {
            // Start activity
            permitCONTACTS();
        });

        // Bottom arrow click listener -> MovementsActivity
        a.lottieArrowBottom = a.findViewById(R.id.lottieArrowBottom);
        a.lottieArrowBottom.setOnClickListener(v -> {
            // Start activity
            permitRECORD_AUDIO();
        });

        // Left arrow click listener -> ConCatActivity [ Category ]
        a.lottieArrowLeft = a.findViewById(R.id.lottieArrowLeft);
        a.lottieArrowLeft.setOnClickListener(v -> {
            if (newValue > 0 && calcSymbol != '?') {
                permitLOOCATION_CAT();
            } else {
                ////////////////////////////////////////
                // Animation [ txtvCalc | txtvResult] //
                ////////////////////////////////////////
                // toast
                toast(activity, WARNING_TOAST, activity.getString(R.string.You_must_enter_a_valid_amount), Toast.LENGTH_SHORT);

                // Animation -> zoom_in_value
                Animation animation = AnimationUtils.loadAnimation(a, R.anim.zoom_in_value);
                a.txtvCalc.startAnimation(animation);
                a.txtvResult.startAnimation(animation);

                // Animation listener
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        ////////////////////////////////////////
                        // Animation [ txtvCalc | txtvResult] //
                        ////////////////////////////////////////
                        // Animation -> zoom_out_value
                        a.txtvCalc.startAnimation(AnimationUtils.loadAnimation(a, R.anim.zoom_out_value));
                        a.txtvResult.startAnimation(AnimationUtils.loadAnimation(a, R.anim.zoom_out_value));
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
            }
        });

        // Right arrow click listener -> ConCatActivity [ Concept ]
        a.lottieArrowRight = a.findViewById(R.id.lottieArrowRight);
        a.lottieArrowRight.setOnClickListener(v -> {
            if (newValue > 0 && calcSymbol != '?') {
                permitLOOCATION_CON();
            } else {
                ////////////////////////////////////////
                // Animation [ txtvCalc | txtvResult] //
                ////////////////////////////////////////
                // toast
                toast(activity, WARNING_TOAST, activity.getString(R.string.You_must_enter_a_valid_amount), Toast.LENGTH_SHORT);

                // Animation -> zoom_in_value
                Animation animation = AnimationUtils.loadAnimation(a, R.anim.zoom_in_value);
                a.txtvCalc.startAnimation(animation);
                a.txtvResult.startAnimation(animation);

                // Animation listener
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        ////////////////////////////////////////
                        // Animation [ txtvCalc | txtvResult] //
                        ////////////////////////////////////////
                        // Animation -> zoom_out_value
                        a.txtvCalc.startAnimation(AnimationUtils.loadAnimation(a, R.anim.zoom_out_value));
                        a.txtvResult.startAnimation(AnimationUtils.loadAnimation(a, R.anim.zoom_out_value));
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
            }
        });

        // Custom long click listener
        final boolean[] run = {false};
        final Handler handler = new Handler();
        Runnable longPressed = () -> {
            if (pointEnabled) {
                run[0] = true;
                pointEnabled = false;
                if (newValue > 0 && calcSymbol != '?') {
                    if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                        // Disable UI
                        disableUI();

                        // Get txtResult and txtName
                        TextView txtResult = ((MainActivity) activity).txtvResult;
                        TextView txtName = ((MainActivity) activity).txtvName;
                        /////////////////////////////////////////////////////

                        // Animation -> fade_out
                        Animation fade_out = AnimationUtils.loadAnimation(activity, R.anim.fade_out_fast);
                        txtResult.startAnimation(fade_out);

                        // Animation listener
                        fade_out.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                // txtResult invisible
                                txtResult.setVisibility(View.INVISIBLE);

                                // Animation -> fade_in
                                Animation fade_in = AnimationUtils.loadAnimation(activity, R.anim.fade_in_fast);
                                txtName.startAnimation(fade_in);

                                // txtName change background, empty text and visible
                                txtName.setBackgroundResource(R.drawable.box_with_round_selected);
                                txtName.setText("");
                                txtName.setVisibility(View.VISIBLE);
                                ////////////////////////////////////

                                fade_in.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        customKeyBoardContactsFast(
                                                keyboardContactsView.findViewById(R.id.textView),
                                                ((MainActivity) activity).txtvName,
                                                R.drawable.box_with_round_selected,
                                                android.R.color.transparent,
                                                keyboardContactsView.findViewById(R.id.rvContacts)
                                        );
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });

                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                    } else {
                        // Need READ_CONTACTS permission
                        allowPermissionsManually(activity, 5);
                        pointEnabled = true;
                    }
                } else {
                    // [DEPRECATED] Maximize or minimize screen
                    //changeScreenSize();

                    // Toast
                    toast(activity, WARNING_TOAST, activity.getString(R.string.You_must_enter_a_valid_amount), Toast.LENGTH_SHORT);
                    pointEnabled = true;
                }
            }
        };

        // Touch listener
        a.btnPoint.setOnTouchListener((v, event) -> {
            if (pointEnabled) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x1 = event.getX();
                        y1 = event.getY();
                        handler.postDelayed(longPressed, ViewConfiguration.getLongPressTimeout());
                        break;
                    case MotionEvent.ACTION_UP:
                        handler.removeCallbacks(longPressed);
                        x2 = event.getX();
                        y2 = event.getY();
                        float deltaX = x2 - x1;
                        float deltaY = y2 - y1;
                        if (Math.abs(deltaY) > MIN_DISTANCE) {
                            // ↑ ↓
                            if (y2 < y1) {
                                /////////////////////////////////////////////
                                // From bottom to top -> MovementsActivity //
                                /////////////////////////////////////////////
                                // Start activity
                                permitRECORD_AUDIO();
                            } else {
                                ////////////////////////////////////////////////
                                // From top to bottom -> TransactionsActivity //
                                ////////////////////////////////////////////////
                                // Start activity
                                permitCONTACTS();
                            }
                            run[0] = false;
                            return true;
                        }

                        if (Math.abs(deltaX) > MIN_DISTANCE) {
                            // ← →
                            if (x2 > x1) {
                                ///////////////////////////////////////////////////////
                                // From right to left -> ConCatActivity [ Category ] //
                                ///////////////////////////////////////////////////////
                                if (newValue > 0 && calcSymbol != '?') {
                                    permitLOOCATION_CAT();
                                } else {
                                    ////////////////////////////////////////
                                    // Animation [ txtvCalc | txtvResult] //
                                    ////////////////////////////////////////
                                    // toast
                                    toast(activity, WARNING_TOAST, activity.getString(R.string.You_must_enter_a_valid_amount), Toast.LENGTH_SHORT);

                                    // Animation -> zoom_in_value
                                    Animation animation = AnimationUtils.loadAnimation(a, R.anim.zoom_in_value);
                                    a.txtvCalc.startAnimation(animation);
                                    a.txtvResult.startAnimation(animation);

                                    // Animation listener
                                    animation.setAnimationListener(new Animation.AnimationListener() {
                                        @Override
                                        public void onAnimationStart(Animation animation) {
                                        }

                                        @Override
                                        public void onAnimationEnd(Animation animation) {
                                            ////////////////////////////////////////
                                            // Animation [ txtvCalc | txtvResult] //
                                            ////////////////////////////////////////
                                            // Animation -> zoom_out_value
                                            a.txtvCalc.startAnimation(AnimationUtils.loadAnimation(a, R.anim.zoom_out_value));
                                            a.txtvResult.startAnimation(AnimationUtils.loadAnimation(a, R.anim.zoom_out_value));
                                        }

                                        @Override
                                        public void onAnimationRepeat(Animation animation) {
                                        }
                                    });
                                    run[0] = false;
                                    return true;
                                }

                            } else {
                                //////////////////////////////////////////////////////
                                // From left to right -> ConCatActivity [ Concept ] //
                                //////////////////////////////////////////////////////
                                if (newValue > 0 && calcSymbol != '?') {
                                    permitLOOCATION_CON();
                                } else {
                                    ////////////////////////////////////////
                                    // Animation [ txtvCalc | txtvResult] //
                                    ////////////////////////////////////////
                                    // toast
                                    toast(activity, WARNING_TOAST, activity.getString(R.string.You_must_enter_a_valid_amount), Toast.LENGTH_SHORT);

                                    // Animation -> zoom_in_value
                                    Animation animation = AnimationUtils.loadAnimation(a, R.anim.zoom_in_value);
                                    a.txtvCalc.startAnimation(animation);
                                    a.txtvResult.startAnimation(animation);

                                    // Animation listener
                                    animation.setAnimationListener(new Animation.AnimationListener() {
                                        @Override
                                        public void onAnimationStart(Animation animation) {
                                        }

                                        @Override
                                        public void onAnimationEnd(Animation animation) {
                                            ////////////////////////////////////////
                                            // Animation [ txtvCalc | txtvResult] //
                                            ////////////////////////////////////////
                                            // Animation -> zoom_out_value
                                            a.txtvCalc.startAnimation(AnimationUtils.loadAnimation(a, R.anim.zoom_out_value));
                                            a.txtvResult.startAnimation(AnimationUtils.loadAnimation(a, R.anim.zoom_out_value));
                                        }

                                        @Override
                                        public void onAnimationRepeat(Animation animation) {
                                        }
                                    });
                                }
                            }
                            run[0] = false;
                            return true;
                        }

                        if (!run[0]) {
                            /////////
                            // Tap //
                            /////////
                            // Refresh text of Calc
                            String strNewValue = a.txtvCalc.getText().toString().split(" ")[2];
                            if (strNewValue.equals("?"))
                                newValue = 0;
                            a.txtvCalc.setText(moneyFormat.format(account.getMoney()) + " " + calcSymbol + " " + moneyFormat.format(newValue));

                            // Refresh text of Result if calcSymbol is different to '?'
                            if (calcSymbol != '?') {
                                newResult = calcSymbol == '+' ? account.getMoney() + newValue : account.getMoney() - newValue;
                                a.txtvResult.setText("= " + moneyFormat.format(newResult));
                            }

                            // Has comma
                            comma = true;
                        }
                }
                run[0] = false;
            }
            return false;
        });
        a.btnPoint.setOnClickListener(v -> { });

        /////////////////////////
        // Calculator boomMenu //
        /////////////////////////
        prepareCalculatorBoomMenu(a);

        //////////////////////
        // Calculator sizes //
        //////////////////////
        a.resizeCalculatorButtons();
    }

    @SuppressLint("SetTextI18n")
    private void calculatorNumberKeyClickEvent(TextView txtvCalc, TextView txtvResult, LottieAnimationView key) {
        // Initialize variables
        String strMoney = txtvCalc.getText().toString().split(" ")[0];
        String strNewValue = txtvCalc.getText().toString().split(" ")[2];
        String num = key.getTag().toString();
        /////////////////////////////////////

        // Build new value
        if (!strNewValue.equals("?")) {
            if (strNewValue.startsWith("0,")) {
                if (strNewValue.equals("0,00")) {
                    strNewValue = "0,0" + num;
                    key.playAnimation();
                } else if (strNewValue.startsWith("0,0")) {
                    strNewValue = "0," + strNewValue.split(",")[1].substring(1) + num;
                    key.playAnimation();
                }
            } else {
                String[] baComma = strNewValue.split(",");
                if (comma) {
                    if (baComma[1].equals("00")) {
                        strNewValue = baComma[0] + ",0" + num;
                        key.playAnimation();
                    } else if (baComma[1].startsWith("0")) {
                        strNewValue = baComma[0] + "," + baComma[1].substring(1) + num;
                        key.playAnimation();
                    }
                } else {
                    if (baComma[0].length() < 8) {
                        strNewValue = baComma[0] + num + "," + baComma[1];
                        key.playAnimation();
                    }
                }
            }
        } else {
            strNewValue = "" + num;
            key.playAnimation();
        }
        //////////////////////////////////////////////

        // Build calc string
        strNewValue = strNewValue.replace(".", "").replace(",", ".");
        newValue = Double.parseDouble(strNewValue);
        String newCalc = moneyFormat.format(account.getMoney()) + " " + calcSymbol + " " + moneyFormat.format(Double.parseDouble(strNewValue));
        txtvCalc.setText(newCalc);
        //////////////////////////

        // Build result string
        if (calcSymbol != '?') {
            strMoney = strMoney.replace(".", "").replace(",", ".");
            if (calcSymbol == '+') {
                if (account.getMoney() < 0)
                    newResult = -Double.parseDouble(strMoney.substring(1)) + Double.parseDouble(strNewValue);
                else newResult = Double.parseDouble(strMoney) + Double.parseDouble(strNewValue);
            } else {
                if (account.getMoney() < 0)
                    newResult = -Double.parseDouble(strMoney.substring(1)) - Double.parseDouble(strNewValue);
                else newResult = Double.parseDouble(strMoney) - Double.parseDouble(strNewValue);
            }
            txtvResult.setText("= " + moneyFormat.format(newResult));
        }
        ////////////////////////////
    }

    @SuppressLint("SetTextI18n")
    private void calculatorCalcSymbolKeyClickEvent(TextView txtvCalc, TextView txtvResult, ImageView key) {
        // Initialize variables
        String strNewValue = txtvCalc.getText().toString().split(" ")[2];
        calcSymbol = key.getTag().toString().charAt(0);
        ///////////////////////////////////////////////

        // Change textViews [ txtvCalc | txtvResult ]
        if (!strNewValue.equals("?")) {
            String newCalc = moneyFormat.format(account.getMoney()) + " " + calcSymbol + " " + moneyFormat.format(newValue);
            txtvCalc.setText(newCalc);
            //
            newResult = (calcSymbol == '+' ? account.getMoney() + newValue : account.getMoney() - newValue);
            txtvResult.setText("= " + moneyFormat.format(newResult));
            //btnEXIN.setImageResource(R.drawable.expense);
            //btnEI.setImageResource(R.drawable.invert);
        } else {
            txtvCalc.setText(moneyFormat.format(account.getMoney()) + " " + calcSymbol + " ?");
        }
    }

    @SuppressLint("SetTextI18n")
    private void calculatorBackKeyClickEvent(TextView txtvCalc, TextView txtvResult, LottieAnimationView key, boolean longClick) {
        // Initialize variables
        String strNewValue = txtvCalc.getText().toString().split(" ")[2];
        ///////////////////////////////////////////////
        if (longClick) {
            if (!strNewValue.equals("?")) {
                txtvCalc.setText(moneyFormat.format(account.getMoney()) + " " + calcSymbol + " ?");
                txtvResult.setText("= ?");
                key.playAnimation();
            }
            newValue = 0.0;
            newResult = 0.0;
            comma = false;
        } else {
            if (!strNewValue.equals("?")) {

                String[] baComma = strNewValue.split(",");

                if (baComma[1].equals("00")) {
                    if (!baComma[0].equals("0")) {
                        if (baComma[0].length() == 1) {
                            strNewValue = "?";
                        } else {
                            strNewValue = baComma[0].substring(0, baComma[0].length() - 1) + ",00";
                        }
                    } else {
                        strNewValue = "?";
                    }
                    comma = false;
                } else if (baComma[1].startsWith("0")) {
                    if (baComma[0].equals("0")) {
                        strNewValue = "?";
                    } else {
                        strNewValue = baComma[0] + ",00";
                    }
                } else {
                    strNewValue = baComma[0] + ",0" + baComma[1].substring(0, 1);
                }

                if (!strNewValue.equals("?")) {
                    newValue = Double.parseDouble(strNewValue.replace(".", "").replace(",", "."));
                    String newCalc = moneyFormat.format(account.getMoney()) + " " + calcSymbol + " " + moneyFormat.format(newValue);
                    txtvCalc.setText(newCalc);
                    if (calcSymbol != '?') {
                        if (calcSymbol == '+') {
                            newResult = account.getMoney() + newValue;
                        } else {
                            newResult = account.getMoney() - newValue;
                        }
                        txtvResult.setText("= " + moneyFormat.format(newResult));
                    }
                } else {
                    newValue = 0.0;
                    txtvCalc.setText(moneyFormat.format(account.getMoney()) + " " + calcSymbol + " ?");
                    txtvResult.setText("= ?");
                    comma = false;
                }
                key.playAnimation();
            }
        }
    }

    private void prepareCalculatorBoomMenu(MainActivity activity) {
        // Find
        BoomMenuButton boom = activity.findViewById(R.id.boom);
        boom.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.zoom_in));
        boom.setBoomEnum(BoomEnum.HORIZONTAL_THROW_1);

        /////////////
        // Prepare //
        /////////////
        boom.setNormalColor(ContextCompat.getColor(activity, R.color.colorApp));
        boom.setHighlightedColor(ContextCompat.getColor(activity, R.color.colorAppLight));
        boom.setButtonEnum(ButtonEnum.Ham);
        ///////////////////////////////////

        // Get count of account
        int count = device.accounts.size();

        // Set structure of boomMenu
        boom.setPiecePlaceEnum(PiecePlaceEnum.HAM_6);
        boom.setButtonPlaceEnum(ButtonPlaceEnum.HAM_6);
        ///////////////////////////////////////////////

        // Declare builder
        HamButton.Builder builder;

        //////////////////
        // Add builders //
        //////////////////
        // Default account
        builder = new HamButton.Builder()
                .normalColor(ContextCompat.getColor(activity, android.R.color.black))
                .normalTextColor(ContextCompat.getColor(activity, R.color.colorExpense))
                .highlightedColor(ContextCompat.getColor(activity, R.color.colorExpense))
                .normalImageRes(R.drawable.individual_user_red)
                .normalText(getNameFormatted(account))
                .subNormalText(activity.getString(R.string.Destroy_account))
                .listener(index -> deleteDefaultAccount());
        boom.addBuilder(builder);
        /////////////////////////

        // Other accounts
        for (int i = 0; i < device.getAccounts().size(); i++) {
            DeviceAccount da = device.getAccounts().get(i);
            if (!da.getId().equals(def)) {
                builder = new HamButton.Builder()
                        .normalColor(ContextCompat.getColor(activity, android.R.color.black))
                        .normalTextColor(ContextCompat.getColor(activity, R.color.colorApp))
                        .highlightedColor(ContextCompat.getColor(activity, R.color.colorApp))
                        .normalImageRes(R.drawable.individual_user)
                        .normalText(getNameFormatted(da))
                        .subNormalText(activity.getString(R.string.Switch_account))
                        .listener(index -> changeAccount(da.getId()));
                boom.addBuilder(builder);
            }
        }

        // More accounts?
        if (count < 3) {
            builder = new HamButton.Builder()
                    .normalColor(ContextCompat.getColor(activity, android.R.color.black))
                    .normalTextColor(ContextCompat.getColor(activity, R.color.colorIncome))
                    .highlightedColor(ContextCompat.getColor(activity, R.color.colorIncome))
                    .normalImageRes(R.drawable.plus_green)
                    .normalText(activity.getString(R.string.More_accounts))
                    .subNormalText(activity.getString(R.string.Build_new_account))
                    .listener(index -> buildNewAccount());
            boom.addBuilder(builder);
        }
        ////////////////////

        // Shop
        builder = new HamButton.Builder()
                .normalColor(ContextCompat.getColor(activity, android.R.color.black))
                .normalTextColor(ContextCompat.getColor(activity, R.color.star))
                .highlightedColor(ContextCompat.getColor(activity, R.color.star))
                .normalImageRes(R.drawable.ic_star_512)
                .normalText("Premium")
                .subNormalText(activity.getString(R.string.See_the_advantages_you_have_with_premium))
                .listener(index -> showShop());
        boom.addBuilder(builder);

        // Security
        builder = new HamButton.Builder()
                .normalColor(ContextCompat.getColor(activity, android.R.color.black))
                .normalTextColor(ContextCompat.getColor(activity, R.color.colorApp))
                .highlightedColor(ContextCompat.getColor(activity, R.color.colorApp))
                .normalImageRes(R.drawable.ic_security_50)
                .normalText(activity.getString(R.string.Password) + ": " + (activity.getString(device.isPassword() ? R.string.Enabled : R.string.Disabled)))
                .subNormalText(activity.getString(R.string.Make_sure_you_have_your_data_protected))
                .listener(index -> changePassword());
        boom.addBuilder(builder);

        // Licenses
        if (device.accounts.size() == 1) {
            builder = new HamButton.Builder()
                    .normalColor(ContextCompat.getColor(activity, android.R.color.black))
                    .normalTextColor(ContextCompat.getColor(activity, R.color.colorIncome))
                    .highlightedColor(ContextCompat.getColor(activity, R.color.colorIncome))
                    .normalImageRes(R.drawable.ic_verified_50)
                    .normalText(activity.getString(R.string.Licenses))
                    .subNormalText(activity.getString(R.string.See_all_image_animation_and_software_licenses))
                    .listener(index -> showLicenses());
            boom.addBuilder(builder);
        }

        // Log out
        builder = new HamButton.Builder()
                .normalColor(ContextCompat.getColor(activity, android.R.color.black))
                .normalTextColor(ContextCompat.getColor(activity, R.color.colorExpense))
                .highlightedColor(ContextCompat.getColor(activity, R.color.colorExpense))
                .normalImageRes(R.drawable.ic_logout_50)
                .normalText(activity.getString(R.string.Log_out))
                .listener(index -> logOut());
        boom.addBuilder(builder);
    }

    /////////////////
    // Accounts UI //
    /////////////////

    public void changeMoney() {
        // Set values
        numsBef = moneyFormat.format(account.getMoney()).split(",")[0];
        numsAft = moneyFormat.format(account.getMoney()).split(",")[1];
        money = account.getMoney();

        // Update money in SQLite
        write().execSQL("update accounts set money = ? where id = ?", new String[]{String.valueOf(account.getMoney()), def});
    }

    @SuppressLint("SetTextI18n")
    public void displayMoney() {
        ///////////////
        // Update UI //
        ///////////////
        TextView txtvCalc = activity.findViewById(R.id.txtCalc);
        TextView txtvResult = activity.findViewById(R.id.txtResult);

        if (isNotNull(txtvCalc) && isNotNull(txtvResult)) {
            if (calcSymbol != '?') {
                String newCalc = moneyFormat.format(account.getMoney()) + " " + calcSymbol + " " + (newValue > 0 ? moneyFormat.format(newValue) : "?");
                txtvCalc.setText(newCalc);
                if (newValue > 0) {
                    if (calcSymbol == '+') newResult = account.getMoney() + newValue;
                    else newResult = account.getMoney() - newValue;
                    txtvResult.setText("= " + moneyFormat.format(newResult));
                } else txtvResult.setText("= ?");
            } else {
                txtvCalc.setText(moneyFormat.format(account.getMoney()) + " " + calcSymbol + " " + (newValue > 0 ? moneyFormat.format(newValue) : "?"));
                txtvResult.setText("= ?");
                comma = false;
            }
        }

    }

    char symbol = '+';

    @SuppressLint("SetTextI18n")
    private void changeAccountMoney() {
        // Build dialog
        changeAccountMoneyDialog = buildDialog(changeAccountMoneyDialog, changeAccountMoneyView, 0, R.style.AppTheme_PopUpBlack, true);

        // Set values
        numsBef = account.getMoney() < 0 ? moneyFormat.format(account.getMoney()).split(",")[0].substring(1) : moneyFormat.format(account.getMoney()).split(",")[0].replace("-", "");
        numsAft = moneyFormat.format(account.getMoney()).split(",")[1];
        money = Math.abs(Math.min(account.getMoney(), MAX_VALUE));
        if (money == MAX_VALUE) {
            numsBef = "9.999.999";
            numsAft = "99";
        }
        currency = account.getCurrency();
        symbol = account.getMoney() > 0 ? '+' : '-';
        ////////////////////////////////////////////

        // Find controls
        TextView txtvNumsBef = changeAccountMoneyView.findViewById(R.id.txtvNumsBef);
        txtvNumsBef.setText(numsBef);
        TextView txtvNumsAft = changeAccountMoneyView.findViewById(R.id.txtvNumsAft);
        txtvNumsAft.setText(numsAft);
        TextView txtvCurrency = changeAccountMoneyView.findViewById(R.id.txtvCurrency);
        txtvCurrency.setText(currency);
        //
        StickySwitch swSymbol = changeAccountMoneyView.findViewById(R.id.swSymbol);
        swSymbol.setDirection(symbol == '-' ? StickySwitch.Direction.LEFT : StickySwitch.Direction.RIGHT);
        //
        SwipeButton swipeBtn = changeAccountMoneyView.findViewById(R.id.swipeBtn);
        //////////////////////////////////////////////////////////////////////////


        // On dismiss
        changeAccountMoneyDialog.setOnDismissListener(dialog -> {
            // Enable UI
            changeAccountMoneyDialog.setCancelable(true);
            txtvNumsBef.setEnabled(true);
            txtvNumsAft.setEnabled(true);
            txtvCurrency.setEnabled(true);
            swSymbol.setEnabled(true);
            swipeBtn.setEnabled(true);
            //////////////////////////
        });

        ////////////////////////
        // Selection listener //
        ////////////////////////
        // swSymbol
        swSymbol.setOnSelectedChangeListener((direction, s) -> {
            symbol = direction == StickySwitch.Direction.LEFT ? '-' : '+';
        });

        /////////////////////
        // Click listeners //
        /////////////////////
        // numbers before comma
        txtvNumsBef.setOnClickListener(v -> customKeyBoard(0, keyboardView.findViewById(R.id.textView), true, false, 7,
                txtvNumsBef,
                R.drawable.box_with_round_selected,
                R.drawable.box_with_round,
                () -> {
                    numsBef = txtvNumsBef.getText().toString();
                    numsAft = txtvNumsAft.getText().toString();
                    try {
                        money = Double.parseDouble(numsBef.replace(".", "") + "." + numsAft);
                    } catch (NumberFormatException ignored) {
                        __wait__("outside_the_system.json");
                    }
                    return null;
                }));

        // numbers after comma
        txtvNumsAft.setOnClickListener(v -> customKeyBoard(0, keyboardView.findViewById(R.id.textView), false, false, 2,
                txtvNumsAft,
                R.drawable.box_with_round_selected,
                R.drawable.box_with_round,
                () -> {
                    numsBef = txtvNumsBef.getText().toString();
                    numsAft = txtvNumsAft.getText().toString();
                    try {
                        money = Double.parseDouble(numsBef.replace(".", "") + "." + numsAft);
                    } catch (NumberFormatException ignored) {
                        __wait__("outside_the_system.json");
                    }
                    return null;
                }));

        // currency
        txtvCurrency.setOnClickListener(v -> customKeyBoard(1, keyboardView.findViewById(R.id.textView), false, false, 3,
                txtvCurrency,
                R.drawable.box_with_round_selected,
                R.drawable.box_with_round,
                () -> {
                    currency = txtvCurrency.getText().toString().toUpperCase();
                    return null;
                }));

        ////////////////////
        // Swipe listener //
        ////////////////////
        // Save changes
        swipeBtn.setOnStateChangeListener(active -> {
            if (active) {
                money = (symbol == '+' ? money : -money);
                // Change account money and currency if it's necessary
                if (!account.getCurrency().equals(currency) || account.getMoney() != money) {
                    // Initialize request
                    StringBuilder requestStringBuilder = new StringBuilder();
                    requestStringBuilder.append("U A ");
                    ////////////////////////////////////

                    if (!account.getCurrency().equals(currency) && currency.length() == 3 && doesThisCurrencyExist(currency)) {
                        // Set new currency
                        account.setCurrency(currency);
                        requestStringBuilder.append(account.getCurrency());
                        ///////////////////////////////////////////////////


                        if (account.getMoney() != money) {
                            // Set new money
                            account.setMoney(money);
                            requestStringBuilder.append(",").append(account.getMoney());
                            ////////////////////////////////////////////////////////////
                        }
                    } else if (account.getMoney() != money) {
                        // Set new money
                        account.setMoney(money);
                        requestStringBuilder.append(account.getMoney());
                        ////////////////////////////////////////////////
                    }

                    // Any changes?
                    if (!requestStringBuilder.toString().equals("U A ")) {
                        // Disable UI
                        changeAccountMoneyDialog.setCancelable(false);
                        txtvNumsBef.setEnabled(false);
                        txtvNumsAft.setEnabled(false);
                        txtvCurrency.setEnabled(false);
                        swSymbol.setEnabled(false);
                        swipeBtn.setEnabled(false);
                        ///////////////////////////

                        // Save account changes
                        saveAccountChanges(requestStringBuilder.toString());
                    } else {
                        // Dismiss dialog
                        dismissDialog(changeAccountMoneyDialog);
                    }
                } else {
                    // Dismiss dialog
                    dismissDialog(changeAccountMoneyDialog);
                }
            }
        });

        // Animate view
        changeAccountMoneyView.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.fade_in));

        // Show dialog
        showDialog(changeAccountMoneyDialog);
    }

    // try -> saveAccountChanges
    private void saveAccountChanges(String request) {
        // Try to connect to the internet
        tryToConnectToTheInternet(() -> {
            if (doesTheDatabaseExist() && !isDatabaseCorrupt()) letSaveAccountChanges(request);
            else {
                FATAL_ERROR = true;
                activity.onBackPressed();
            }
            return null;
        });
    }

    // let -> saveAccountChanges
    private void letSaveAccountChanges(String request) {
        // Send request
        newRequest(config.isBm(), accountPos(), request, () -> {
            // Connect to SQLite
            connect();

            // Execute request in SQLite
            write().execSQL("update accounts set currency = ?, money = ? where id = ?", new String[]{account.getCurrency(), String.valueOf(account.getMoney()), def});

            ///////////////
            // Update UI //
            ///////////////
            // Display money
            displayMoney();

            // Dismiss dialog
            dismissDialog(changeAccountMoneyDialog);
            return null;
        });
    }

    // try -> deleteDefaultAccount
    private void deleteDefaultAccount() {

        // Set content view
        activity.setContentView(R.layout.waiting_for_authentication);
        ////////////////////////////////////////////////////

        // Controls
        ImageView imgvWFAAstronaut = activity.findViewById(R.id.imgvWFAAstronaut);
        ImageView imgvWFAPoint = activity.findViewById(R.id.imgvWFAPoint);
        ImageView imgvWFATop = activity.findViewById(R.id.imgvWFATop);
        ImageView imgvWFABottom = activity.findViewById(R.id.imgvWFABottom);
        ImageView imgvWFALeft = activity.findViewById(R.id.imgvWFALeft);
        ImageView imgvWFARight = activity.findViewById(R.id.imgvWFARight);
        TextView txtvWFAAction = activity.findViewById(R.id.txtvWFAAction);
        TextView txtvWFAAppName = activity.findViewById(R.id.txtvWFAAppName);
        LottieAnimationView lottieLoading = activity.findViewById(R.id.lottieLoading);
        /////////////////////////////////////////////////////////////////////

        // Visibility -> GONE
        txtvWFAAction.setVisibility(View.GONE);
        imgvWFALeft.setVisibility(View.GONE);
        imgvWFATop.setVisibility(View.GONE);
        imgvWFARight.setVisibility(View.GONE);
        imgvWFABottom.setVisibility(View.GONE);
        imgvWFAPoint.setVisibility(View.GONE);
        //////////////////////////////////////

        // Fade in
        Animation fadeIn = AnimationUtils.loadAnimation(activity, R.anim.fade_in);
        imgvWFAAstronaut.setAnimation(fadeIn);
        txtvWFAAppName.setAnimation(fadeIn);
        ////////////////////////////////////

        // Visibility -> VISIBLE
        imgvWFAAstronaut.setVisibility(View.VISIBLE);
        txtvWFAAppName.setVisibility(View.VISIBLE);
        lottieLoading.setVisibility(View.VISIBLE);
        //////////////////////////////////////////

        // Try to connect to the internet
        tryToConnectToTheInternet(() -> {
            if (doesTheDatabaseExist() && !isDatabaseCorrupt()) letDeleteDefaultAccount();
            else {
                FATAL_ERROR = true;
                restartApp();
            }
            return null;
        });
    }

    // let -> deleteDefaultAccount
    private void letDeleteDefaultAccount() {
        //////////////////////////////////
        // Delete account from Firebase //
        //////////////////////////////////
        // Clear auxR
        device.accounts.get(accountIndex()).auxR = new ArrayList<>();

        // Clear r
        device.accounts.get(accountIndex()).r = new ArrayList<>();

        // Add request of 'delete' to -> [ auxR || r ] -> it depends on config.isBm()
        if (config.isBm()) {
            // Add request to auxR -> 'D'
            device.accounts.get(accountIndex()).auxR.add("D");
        } else {
            // Add request to r -> 'D'
            device.accounts.get(accountIndex()).r.add("D");
        }

        // Set type
        String type = config.isBm() ? "auxR" : "r";

        // Get account of device
        DeviceAccount da = device.getAccounts().get(accountIndex());

        // Delete account in Firebase + SQLite
        FirebaseDatabase.getInstance().getReference("devices/" + devId + "/accounts/" + da.getPos() + "/" + type).setValue(config.isBm() ? da.getAuxR() : da.getR()).addOnSuccessListener(aVoid -> {
            // Add ghost account + remove account
            if (ghostAccounts == null) ghostAccounts = new ArrayList<>();
            DeviceAccount ghostAccount = new DeviceAccount(def, accountIndex());
            ghostAccounts.add(ghostAccount);
            device.accounts.remove(accountIndex());
            ///////////////////////////////////////

            ////////////////////////////////
            // Delete account from SQLite //
            ////////////////////////////////
            // Connect to SQLite
            connect();

            // Delete movements
            write().delete("movements", "accountId=?", new String[]{def});

            // Delete concepts
            write().delete("concepts", "accountId=?", new String[]{def});

            // Delete account
            write().delete("accounts", "id=?", new String[]{def});
            ////////////////////////////////////////////////////////////////////////

            // Set def as empty
            def = "";

            // Rewrite configuration
            rewriteConfig();

            // Need new account?
            if (device.getAccounts().size() == 0) buildNewAccount();
            else /* Restart app */ restartApp();
        });
    }

    private void buildNewAccount() {
        if (device.isPremium() || (device.accounts.size() == 0)) {
            if (!doesTheDatabaseExist() || !exceedsTheMaxLimitOf("accounts", true)) {
                // Initialize dialog
                buildAccountDialog = buildDialog(buildAccountDialog, buildAccountView, 0, R.style.AppTheme_PopUpBlack, device.accounts.size() > 0);

                // Set values
                name = "";
                numsBef = "0";
                numsAft = "00";
                money = 0.0;
                ////////////

                // Find controls
                TextView txtvName = buildAccountView.findViewById(R.id.txtvName);
                txtvName.setText(name);
                TextView txtvNumsBef = buildAccountView.findViewById(R.id.txtvNumsBef);
                txtvNumsBef.setText(numsBef);
                TextView txtvNumsAft = buildAccountView.findViewById(R.id.txtvNumsAft);
                txtvNumsAft.setText(numsAft);
                TextView txtvCurrency = buildAccountView.findViewById(R.id.txtvCurrency);
                txtvCurrency.setText(currency);
                //
                LottieAnimationView lottieInfoName = buildAccountView.findViewById(R.id.lottieInfoName);
                LottieAnimationView lottieInfoMoney = buildAccountView.findViewById(R.id.lottieInfoMoney);
                TextView txtvInfoName = buildAccountView.findViewById(R.id.txtvInfoName);
                TextView txtvInfoMoney = buildAccountView.findViewById(R.id.txtvInfoMoney);
                if (device.accounts.size() == 0) {
                    lottieInfoName.setVisibility(View.VISIBLE);
                    lottieInfoMoney.setVisibility(View.VISIBLE);
                    txtvInfoName.setVisibility(View.VISIBLE);
                    txtvInfoMoney.setVisibility(View.VISIBLE);
                } else {
                    lottieInfoName.setVisibility(View.INVISIBLE);
                    lottieInfoMoney.setVisibility(View.INVISIBLE);
                    txtvInfoName.setVisibility(View.INVISIBLE);
                    txtvInfoMoney.setVisibility(View.INVISIBLE);
                }
                //
                SwipeButton swipeBtn = buildAccountView.findViewById(R.id.swipeBtn);
                ////////////////////////////////////////////////////////////////////

                ///////////////////////////////
                // TextViews click listeners //
                ///////////////////////////////
                // name
                txtvName.setOnClickListener(v -> customKeyBoard(1, keyboardView.findViewById(R.id.textView), false, false, 10,
                        txtvName,
                        R.drawable.box_with_round_selected,
                        R.drawable.box_with_round,
                        () -> {
                            name = txtvName.getText().toString();
                            return null;
                        }));

                // numbers before comma
                txtvNumsBef.setOnClickListener(v -> customKeyBoard(0, keyboardView.findViewById(R.id.textView), true, false, 7,
                        txtvNumsBef,
                        R.drawable.box_with_round_selected,
                        R.drawable.box_with_round,
                        () -> {
                            numsBef = txtvNumsBef.getText().toString();
                            numsAft = txtvNumsAft.getText().toString();
                            try {
                                money = Double.parseDouble(numsBef.replace(".", "") + "." + numsAft);
                            } catch (NumberFormatException ignored) {
                                __wait__("outside_the_system.json");
                            }
                            return null;
                        }));

                // numbers after comma
                txtvNumsAft.setOnClickListener(v -> customKeyBoard(0, keyboardView.findViewById(R.id.textView), false, false, 2,
                        txtvNumsAft,
                        R.drawable.box_with_round_selected,
                        R.drawable.box_with_round,
                        () -> {
                            numsBef = txtvNumsBef.getText().toString();
                            numsAft = txtvNumsAft.getText().toString();
                            try {
                                money = Double.parseDouble(numsBef.replace(".", "") + "." + numsAft);
                            } catch (NumberFormatException ignored) {
                                __wait__("outside_the_system.json");
                            }
                            return null;
                        }));

                // currency
                txtvCurrency.setOnClickListener(v -> customKeyBoard(1, keyboardView.findViewById(R.id.textView), false, false, 3,
                        txtvCurrency,
                        R.drawable.box_with_round_selected,
                        R.drawable.box_with_round,
                        () -> {
                            currency = txtvCurrency.getText().toString().toUpperCase();
                            return null;
                        }));

                // On swipe -> build account
                swipeBtn.setOnStateChangeListener(active -> {
                    if (active) {
                        if (isAccountDataCorrect()) {
                            // Disable UI
                            txtvName.setEnabled(false);
                            txtvNumsBef.setEnabled(false);
                            txtvNumsAft.setEnabled(false);
                            txtvCurrency.setEnabled(false);
                            swipeBtn.setEnabled(false);
                            ///////////////////////////

                            // New account
                            newAccount();
                        } else
                            toast(activity, WARNING_TOAST, activity.getString(R.string.The_data_entered_is_not_correct), Toast.LENGTH_SHORT);
                    }
                });

                // Animate view
                buildAccountView.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.fade_in));

                // Show dialog
                showDialog(buildAccountDialog);
            } else dismissDialog(buildAccountDialog);
        } else
            toast(activity, CONFIRMATION_TOAST, activity.getString(R.string.Go_premium_and_you_can_enjoy_this_option), Toast.LENGTH_SHORT);
    }

    // try -> newAccount
    private void newAccount() {
        // Try to connect to the internet
        tryToConnectToTheInternet(() -> {
            if (!doesTheDatabaseExist() || !isDatabaseCorrupt()) letNewAccount();
            else {
                dismissDialog(buildAccountDialog);
                FATAL_ERROR = true;
                restartApp();
            }
            return null;
        });
    }

    // let -> newAccount
    private void letNewAccount() {
        // Init DeviceAccount of device
        DeviceAccount da = new DeviceAccount(name.toLowerCase(), device.accounts.size() + (isNotNull(ghostAccounts) ? ghostAccounts.size() : 0));

        // Init account
        Account account = new Account();
        account.setId(name.toLowerCase() + "#" + device.getCountry() + "_" + device.getPhone());
        account.setCurrency(currency);
        account.setMoney(money);
        ////////////////////////

        // Initialize request
        String request = "I A " + account.getCurrency() + "," + account.getMoney();

        // Add request
        if (!config.isBm()) {
            if (da.r == null)
                da.r = new ArrayList<>();
            da.getR().add(request);
        } else {
            if (da.auxR == null)
                da.auxR = new ArrayList<>();
            da.getAuxR().add(request);
        }

        // Insert account in FirebaseDatabase
        FirebaseDatabase.getInstance().getReference("devices/" + devId + "/accounts/" + da.getPos()).setValue(da).addOnSuccessListener(aVoid -> {
            // Add new account
            if (isNull(device.accounts)) device.accounts = new ArrayList<>();
            device.accounts.add(da);
            ////////////////////////

            // Broke account id
            brokeAccountIds();

            // Build account id
            buildAccountIds();

            // Connect to SQLite
            connect();

            // Insert account in SQLite
            write().execSQL("insert into accounts (id, currency, money) values(?, ?, ?)", new String[]{account.getId(), account.getCurrency(), String.valueOf(account.getMoney())});

            // Restart app
            restartApp();

            // Dismiss dialog
            buildAccountView.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.fade_out));
            buildAccountView.getAnimation().setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    dismissDialog(buildAccountDialog);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        });
    }

    private boolean doesThisAccountExist(String name) {
        return device.accounts.stream().anyMatch(a -> a.getId().startsWith(name.toLowerCase() + "#"));
    }

    private boolean isAccountDataCorrect() {
        return len(name) > 0 && len(name) <= 10 && !doesThisAccountExist(name) && currency.length() == 3 && doesThisCurrencyExist(currency);
    }

    private void changeAccount(String id) {
        // Set default account
        setDefaultAccount(id);

        // Restart App
        restartApp();
    }
    ///////////////////////////////////////////

    /////////////
    // Show UI //
    /////////////
    @SuppressLint("SetTextI18n")
    private void showShop() {
        // Initialize dialog
        shopDialog = buildDialog(shopDialog, shopView, 0, R.style.AppTheme_PopUpBlack, true);

        // Find controls
        TextView txtvPremiumSoftwareDescription = shopView.findViewById(R.id.txtvPremiumSoftwareDescription);
        txtvPremiumSoftwareDescription.setText(activity.getString(R.string.Group_payment_creation) + "\n" +
                activity.getString(R.string.Voice_motion_filtering));

        TextView txtvPremium0 = shopView.findViewById(R.id.txtvPremium0);
        View divPremium = shopView.findViewById(R.id.divPremium);
        ImageView imgvPremium = shopView.findViewById(R.id.imgvPremium);
        TextView txtvPremium1 = shopView.findViewById(R.id.txtvPremium1);
        TextView txtvPremium2 = shopView.findViewById(R.id.txtvPremium2);
        //
        View divLeft = shopView.findViewById(R.id.divLeft);
        TextView txtvOr = shopView.findViewById(R.id.txtvOr);
        View divRight = shopView.findViewById(R.id.divRight);
        //
        SwipeButton swipeBtn = shopView.findViewById(R.id.swipeBtn);
        SeekBar seekBarDonate = shopView.findViewById(R.id.seekBarDonate);
        //
        TextView txtvDonate = shopView.findViewById(R.id.txtvDonate);
        //
        Button btnDonate1, btnDonate2, btnDonate3, btnDonate4, btnDonate5, btnDonate6;
        ArrayList<Button> donateButtons = new ArrayList<>();
        btnDonate1 = shopView.findViewById(R.id.btnDonate1);
        donateButtons.add(btnDonate1);
        btnDonate2 = shopView.findViewById(R.id.btnDonate2);
        donateButtons.add(btnDonate2);
        btnDonate3 = shopView.findViewById(R.id.btnDonate3);
        donateButtons.add(btnDonate3);
        btnDonate4 = shopView.findViewById(R.id.btnDonate4);
        donateButtons.add(btnDonate4);
        btnDonate5 = shopView.findViewById(R.id.btnDonate5);
        donateButtons.add(btnDonate5);
        btnDonate6 = shopView.findViewById(R.id.btnDonate6);
        donateButtons.add(btnDonate6);
        //
        txtvDonate.setText(activity.getString(R.string.Donate) + ": " + moneyFormat.format(seekBarDonate.getProgress()) + " €");
        for (Button b : donateButtons) {
            b.setOnClickListener(v -> {
                Button btn = donateButtons.stream().filter(button -> !button.isEnabled()).findFirst().get();
                btn.setEnabled(true);
                btn.setBackgroundResource(R.drawable.keyboard_key);
                //
                b.setEnabled(false);
                b.setBackgroundResource(R.drawable.round_blue);
                //
                seekBarDonate.setMin(Integer.parseInt(b.getTag().toString()));
                seekBarDonate.setMax(10 * Integer.parseInt(b.getTag().toString()));
                seekBarDonate.setProgress(seekBarDonate.getMin());
                txtvDonate.setText(activity.getString(R.string.Donate) + ": " + moneyFormat.format(seekBarDonate.getProgress()) + " €");
            });
        }

        if (device.isPremium()) {
            // Set visibility
            txtvPremium0.setVisibility(View.INVISIBLE);
            divPremium.setVisibility(View.INVISIBLE);
            imgvPremium.setVisibility(View.INVISIBLE);
            txtvPremium1.setVisibility(View.INVISIBLE);
            txtvPremium2.setVisibility(View.INVISIBLE);
            //
            divLeft.setVisibility(View.INVISIBLE);
            txtvOr.setVisibility(View.INVISIBLE);
            divRight.setVisibility(View.INVISIBLE);
            //
            swipeBtn.setVisibility(View.GONE);
            //////////////////////////////////
        } else {
            // Set visibility
            txtvPremium0.setVisibility(View.VISIBLE);
            divPremium.setVisibility(View.VISIBLE);
            imgvPremium.setVisibility(View.VISIBLE);
            txtvPremium1.setVisibility(View.VISIBLE);
            txtvPremium2.setVisibility(View.VISIBLE);
            //
            divLeft.setVisibility(View.VISIBLE);
            txtvOr.setVisibility(View.VISIBLE);
            divRight.setVisibility(View.VISIBLE);
            //
            swipeBtn.setVisibility(View.VISIBLE);
            /////////////////////////////////////

            // On swipe listener
            swipeBtn.setOnStateChangeListener(active -> {
                if (active) {
                    allowPayPalPayment("SRV/config/shop/premium", "Premium");
                    dismissDialog(shopDialog);
                }
            });
        }

        // On seek bar change listener
        seekBarDonate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtvDonate.setText(activity.getString(R.string.Donate) + ": " + moneyFormat.format(progress) + " €");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                allowPayPalPayment(seekBarDonate.getProgress(), activity.getString(R.string.Donation));
                dismissDialog(shopDialog);
            }
        });

        // Animate view
        shopView.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.fade_in));

        // Show dialog
        showDialog(shopDialog);
    }

    @SuppressLint("SetTextI18n")
    private void changePassword() {
        if (isNetworkAvailable()) {
            // Change password
            set("devices/" + devId + "/password", !device.isPassword());
            device.setPassword(!device.isPassword());

            // Display changes
            BoomMenuButton boom = activity.findViewById(R.id.boom);
            boom.getBoomButton(device.accounts.size() == 1 ? boom.getBoomButtons().size() - 3 : boom.getBoomButtons().size() - 2).getTextView().setText(activity.getString(R.string.Password) + ": " + (activity.getString(device.isPassword() ? R.string.Enabled : R.string.Disabled)));
        } else
            toast(activity, WARNING_TOAST, activity.getString(R.string.It_has_not_been_possible_to_perform_this_action), Toast.LENGTH_SHORT);
    }

    private void showLicenses() {
        Intent i = new Intent(activity, LicensesActivity.class);
        putData(i);
        activity.startActivity(i);
        activity.overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
    }

    private void showPrivacyPolicy() {
        if (isNetworkAvailable()) {
            String fileName = getPrivacyPolicyFileName();
            File privacyPolicy = new File(activity.getFilesDir(), fileName);
            FirebaseStorage.getInstance().getReference("SRV/app/" + fileName).getFile(privacyPolicy)
                    .addOnSuccessListener(taskSnapshot -> {
                        if (privacyPolicy.exists()) showText(readAllText(privacyPolicy));
                    })
                    .addOnFailureListener(e -> toast(activity, WARNING_TOAST, activity.getString(R.string.Something_went_wrong), Toast.LENGTH_SHORT));
        } else
            toast(activity, WARNING_TOAST, activity.getString(R.string.It_has_not_been_possible_to_download_the_privacy_policy__check_your_Internet_connection), Toast.LENGTH_SHORT);
    }

    private String getPrivacyPolicyFileName() {
        String fileName = "privacy_policy";
        if (getSystemISO3Language().equals("eus")) {
            fileName += "_" + getSystemISO3Language();
        } else if (getSystemISO3Language().equals("spa")) {
            fileName += "_esp";
        }
        fileName += ".txt";
        return fileName;
    }

    private void showText(String text) {
        // Initialize dialog
        scrollableTextViewDialog = buildDialog(scrollableTextViewDialog, scrollableTextView, R.style.FadeAnimation, R.style.AppTheme_PopUp, true);

        // Find controls
        TextView txtvScrollable = scrollableTextView.findViewById(R.id.txtvScrollable);
        txtvScrollable.setMovementMethod(new ScrollingMovementMethod());

        // Set text
        txtvScrollable.setText(text);

        // Show dialog
        showDialog(scrollableTextViewDialog);
    }

    private void logOut() {
        FirebaseFirestore.getInstance().collection("devices").document(devId).delete();
    }

    /////////////////////////////

    BiometricPrompt biometricPrompt;
    BiometricPrompt.PromptInfo promptInfo;

    private void checkPassword() {
        biometricPrompt = new BiometricPrompt((FragmentActivity) activity, ContextCompat.getMainExecutor(activity), new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                activity.onBackPressed();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                hideSystemUI(activity);
                permitAccess();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle(activity.getString(R.string.app_name))
                .setSubtitle(activity.getString(R.string.Security_system))
                .setNegativeButtonText(activity.getString(R.string.cancel))
                .build();

        biometricPrompt.authenticate(promptInfo);
    }

    @SuppressLint("InflateParams")
    public void initViews() {
        lottieView = activity.getLayoutInflater().inflate(R.layout.dialog_lottie, null);
        dismissDialog(lottieDialog);
        lottieDialog = null;

        infoView = activity.getLayoutInflater().inflate(R.layout.dialog_info, null);
        dismissDialog(infoDialog);
        infoDialog = null;

        keyboardView = activity.getLayoutInflater().inflate(R.layout.dialog_keyboard, null);
        dismissDialog(keyboardDialog);
        keyboardDialog = null;
        findKeysOfKeyboard();

        keyboardContactsView = activity.getLayoutInflater().inflate(R.layout.dialog_keyboard_contacts, null);
        dismissDialog(keyboardContactsDialog);
        keyboardContactsDialog = null;
        findKeysOfKeyboardContacts();

        keyboardConceptsView = activity.getLayoutInflater().inflate(R.layout.dialog_keyboard_concepts, null);
        dismissDialog(keyboardConceptsDialog);
        keyboardConceptsDialog = null;
        findKeysOfKeyboardConcepts();

        microphoneView = activity.getLayoutInflater().inflate(R.layout.dialog_microphone, null);
        dismissDialog(microphoneDialog);
        microphoneDialog = null;

        shopView = activity.getLayoutInflater().inflate(R.layout.dialog_shop, null);
        dismissDialog(shopDialog);
        shopDialog = null;

        buildAccountView = activity.getLayoutInflater().inflate(R.layout.dialog_build_account, null);
        dismissDialog(buildAccountDialog);
        buildAccountDialog = null;

        appInfoView = activity.getLayoutInflater().inflate(R.layout.dialog_build_account, null);
        dismissDialog(appInfoDialog);
        appInfoDialog = null;

        changeAccountMoneyView = activity.getLayoutInflater().inflate(R.layout.dialog_change_account_money, null);
        dismissDialog(changeAccountMoneyDialog);
        changeAccountMoneyDialog = null;

        recyclerView = activity.getLayoutInflater().inflate(R.layout.dialog_recycler, null);
        dismissDialog(recyclerDialog);
        recyclerDialog = null;

        scrollableTextView = activity.getLayoutInflater().inflate(R.layout.dialog_scrollable_textview, null);
        dismissDialog(scrollableTextViewDialog);
        scrollableTextViewDialog = null;
    }

    public void dismissDialog(AlertDialog dialog) {
        if (isNotNull(dialog) && dialog.isShowing()) {
            try {
                dialog.dismiss();
            } catch (Exception ignored) {
            }
        }
    }

    public void dismissDialog() {
        if (isNotNull(lottieDialog) && lottieDialog.isShowing()) {
            try {
                lottieDialog.dismiss();
            } catch (Exception ignored) {
            }
        }
    }

    public void showLayout(ConstraintLayout constraintLayout) {
        constraintLayout.setVisibility(View.VISIBLE);
    }

    public void hideLayout(ConstraintLayout constraintLayout) {
        constraintLayout.setVisibility(View.GONE);
    }

    // TOAST TYPES ///////////////////////////////////
    // Info                                         //
    public static final int INFO_TOAST = 0;         //
    //                                              //
    // Warning                                      //
    public static final int WARNING_TOAST = 1;      //
    //                                              //
    // Confirmation                                 //
    public static final int CONFIRMATION_TOAST = 2; //
    //////////////////////////////////////////////////

    public void toast(Activity activity, int type, String message, int duration) {

        @SuppressLint("InflateParams") View v = activity.getLayoutInflater().inflate(R.layout.toast_info, null);

        LottieAnimationView lottie = v.findViewById(R.id.lottie);
        TextView txtvMessage = v.findViewById(R.id.txtvMessage);

        switch (type) {
            case INFO_TOAST:
                lottie.setAnimation("info.json");
                lottie.setBackground(ContextCompat.getDrawable(activity, R.drawable.info_toast_black_round_lottie));
                txtvMessage.setBackground(ContextCompat.getDrawable(activity, R.drawable.info_toast_black_round_message));
                break;
            case WARNING_TOAST:
                lottie.setAnimation("warning.json");
                lottie.setBackground(ContextCompat.getDrawable(activity, R.drawable.warning_toast_black_round_lottie));
                txtvMessage.setBackground(ContextCompat.getDrawable(activity, R.drawable.warning_toast_black_round_message));
                break;
            case CONFIRMATION_TOAST:
                lottie.setAnimation("confirmation.json");
                lottie.setBackground(ContextCompat.getDrawable(activity, R.drawable.confirmation_toast_black_round_lottie));
                txtvMessage.setBackground(ContextCompat.getDrawable(activity, R.drawable.confirmation_toast_black_round_message));
                break;
        }

        txtvMessage.setText(message);

        Toast t = new Toast(activity);
        t.setView(v);
        t.setDuration(duration);
        t.show();
    }

    public void toastDebug(Object... args) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Object arg : args) {
            stringBuilder.append(arg.getClass()).append(": ").append(arg).append("\n");
        }
        toast(activity, INFO_TOAST, stringBuilder.toString(), Toast.LENGTH_SHORT);
    }

    public static void setAdapter(RecyclerView recyclerView, RecyclerView.Adapter adapter, boolean hasFixedSize, boolean nestedScrollingEnabled, LinearLayoutManager linearLayoutManager) {
        recyclerView.setHasFixedSize(hasFixedSize);
        recyclerView.setNestedScrollingEnabled(nestedScrollingEnabled);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    public static void setAdapter(RecyclerView recyclerView, RecyclerView.Adapter adapter, boolean hasFixedSize, boolean nestedScrollingEnabled, GridLayoutManager gridLayoutManager) {
        recyclerView.setHasFixedSize(hasFixedSize);
        recyclerView.setNestedScrollingEnabled(nestedScrollingEnabled);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    public void enableBottomNavView(BottomNavigationView bottomNavView, boolean enabled) {
        for (int i = 0; i < bottomNavView.getMenu().size(); i++)
            bottomNavView.getMenu().getItem(i).setEnabled(enabled);
    }

    @SuppressLint("ClickableViewAccessibility")
    public void enablePagersPagination(CustomViewPager viewPager, TabLayout tabLayout, boolean enabled) {
        // ViewPager
        viewPager.setPagingEnabled(enabled);

        // TabLayout
        LinearLayout tabStrip = ((LinearLayout) tabLayout.getChildAt(0));
        for (int i = 0; i < tabStrip.getChildCount(); i++) {
            tabStrip.getChildAt(i).setOnTouchListener((v, event) -> !enabled);
        }
    }

    class NonSwipeableViewPager extends ViewPager {

        public NonSwipeableViewPager(Context context) {
            super(context);
            setMyScroller();
        }

        public NonSwipeableViewPager(Context context, AttributeSet attrs) {
            super(context, attrs);
            setMyScroller();
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent event) {
            // Never allow swiping to switch between pages
            return false;
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            // Never allow swiping to switch between pages
            return false;
        }

        //down one is added for smooth scrolling

        private void setMyScroller() {
            try {
                Class<?> viewpager = ViewPager.class;
                Field scroller = viewpager.getDeclaredField("mScroller");
                scroller.setAccessible(true);
                scroller.set(this, new MyScroller(getContext()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public class MyScroller extends Scroller {
            public MyScroller(Context context) {
                super(context, new DecelerateInterpolator());
            }

            @Override
            public void startScroll(int startX, int startY, int dx, int dy, int duration) {
                super.startScroll(startX, startY, dx, dy, 350);
            }
        }
    }

    public static class RecyclerViewDisabler implements RecyclerView.OnItemTouchListener {

        @Override
        public boolean onInterceptTouchEvent(@NotNull RecyclerView rv, @NotNull MotionEvent e) {
            return true;
        }

        @Override
        public void onTouchEvent(@NotNull RecyclerView rv, @NotNull MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }
    }


    private DecimalFormat df() {
        DecimalFormat decimalFormat = new DecimalFormat("#,##");
        decimalFormat.setGroupingUsed(true);
        decimalFormat.setGroupingSize(3);
        return decimalFormat;
    }

    public int len(String string) {
        return string != null ? string.length() : 0;
    }


    // Contacts utilities
    @SuppressLint("Recycle")
    public String findContactByLookupKey(String lookupKey) {
        @SuppressLint("Recycle") ContentProviderClient contentProviderClient = activity.getContentResolver().acquireContentProviderClient(ContactsContract.Contacts.CONTENT_URI);

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
        return cur != null && cur.moveToFirst() ? cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)) : activity.getString(R.string.Not_found);
    }

    String friendLookupKey;

    public Map<String, String> findContactsByName(String name) {
        Map<String, String> contacts = new HashMap<>();


        @SuppressLint("Recycle") ContentProviderClient mCProviderClient = activity.getContentResolver().acquireContentProviderClient(ContactsContract.Contacts.CONTENT_URI);

        try {
            Cursor c = mCProviderClient.query(ContactsContract.Contacts.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " LIKE ?" + (friendLookupKey != null ? " AND " + ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY + " NOT LIKE ?" : ""),
                    friendLookupKey != null ? new String[]{name + "%", friendLookupKey + "%"} : new String[]{name + "%"},
                    null);
            if (c != null && c.getCount() > 0) {
                while (c.moveToNext()) {
                    if (c.getInt(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                        // Get lookupKey
                        String lookupKey = c.getString(c.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY)).split("\\.")[0].split("-")[0];

                        // Get display name
                        String displayName = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));

                        // Calculate trending
                        double trending = calculateTrending(lookupKey);

                        // Add values to dictionary
                        contacts.put(lookupKey, trending == 0 ? displayName : displayName + "  " + moneyFormat.format(trending));
                    }
                }
            }

            if (c != null) c.close();
        } catch (RemoteException ignored) {
        }

        return contacts;
    }

    public Map<String, String> findContactsByName(String name, ArrayList<String> friendLookUpKeys) {
        Map<String, String> contacts = new HashMap<>();

        StringBuilder lookUpKeys = null;
        if (friendLookUpKeys != null && friendLookUpKeys.size() > 0) {
            lookUpKeys = new StringBuilder();
            for (String friendLookUpKey : friendLookUpKeys)
                lookUpKeys.append(ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY).append(" NOT LIKE '").append(friendLookUpKey).append("%'").append(" AND ");
            lookUpKeys = new StringBuilder(lookUpKeys.substring(0, lookUpKeys.length() - 5));
        }

        @SuppressLint("Recycle") ContentProviderClient mCProviderClient = activity.getContentResolver().acquireContentProviderClient(ContactsContract.Contacts.CONTENT_URI);

        try {
            Cursor c = mCProviderClient.query(ContactsContract.Contacts.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " LIKE ?" +
                            (lookUpKeys != null ? " AND " + lookUpKeys.toString() : ""),
                    new String[]{name + "%"},
                    null);
            if (c != null && c.getCount() > 0) {
                while (c.moveToNext()) {
                    if (c.getInt(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                        // Get lookupKey
                        String lookupKey = c.getString(c.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY)).split("\\.")[0].split("-")[0];

                        // Get display name
                        String displayName = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));

                        // Calculate trending
                        double trending = calculateTrending(lookupKey);

                        // Add values to dictionary
                        contacts.put(lookupKey, trending == 0 ? displayName : displayName + "  " + moneyFormat.format(trending));
                    }
                }
            }

            if (c != null) c.close();
        } catch (RemoteException ignored) {
        }

        return contacts;
    }

    public ArrayList<Concept> findConceptsByName(String name, ArrayList<Concept> concepts) {
        return concepts.stream().filter(c -> c.getName().toLowerCase().startsWith(name.toLowerCase())).collect(Collectors.toCollection(ArrayList::new));
    }

    private double calculateTrending(String lookupKey) {
        // Connect to SQLite
        connect();

        // Cursor
        @SuppressLint("Recycle") Cursor c = read().rawQuery("select sum(value) from movements where accountId = ? " +
                "and location = ? and type = 'TR'", new String[]{account.getId(), lookupKey});
        return c.moveToFirst() ? c.getDouble(0) : 0;
    }

    public String getGroupName(int id) {
        if (id != -1) {
            // Connect to SQLite
            connect();

            // Get name of group payment
            @SuppressLint("Recycle") Cursor c = read().rawQuery("select location from movements where id = ?", new String[]{String.valueOf(id)});
            return c.moveToFirst() ? c.getString(0) : null;
        }
        return null;
    }

    public String getConceptName(int id) {
        if (id != -1) {
            // Connect to SQLite
            connect();

            // Get name of group payment
            @SuppressLint("Recycle") Cursor c = read().rawQuery("select location from movements where id = ?", new String[]{String.valueOf(id)});
            return c.moveToFirst() ? c.getString(0) : null;
        }
        return null;
    }

    // parseToLocalDateTime
    public LocalDateTime parseToLocalDateTime(String date) {
        return LocalDateTime.parse(date, DateTimeFormatter.ofPattern(System.datePattern));
    }

    // parseToString
    public String parseLocalDateTimeToString(LocalDateTime date) {
        return date.format(DateTimeFormatter.ofPattern(datePattern));
    }

    // getMinDate
    public LocalDateTime getMinDate(ArrayList<Payment> payments) {
        return payments.stream().filter(Objects::nonNull).map(p -> LocalDateTime.parse(p.getDate(), DateTimeFormatter.ofPattern(System.datePattern))).min(LocalDateTime::compareTo).isPresent() ?
                payments.stream().filter(Objects::nonNull).map(p -> LocalDateTime.parse(p.getDate(), DateTimeFormatter.ofPattern(System.datePattern))).min(LocalDateTime::compareTo).get()
                :
                null;
    }

    // getMaxDate
    public LocalDateTime getMaxDate(ArrayList<Payment> payments) {
        return payments.stream().filter(Objects::nonNull).map(p -> LocalDateTime.parse(p.getDate(), DateTimeFormatter.ofPattern(System.datePattern))).max(LocalDateTime::compareTo).isPresent() ?
                payments.stream().filter(Objects::nonNull).map(p -> LocalDateTime.parse(p.getDate(), DateTimeFormatter.ofPattern(System.datePattern))).max(LocalDateTime::compareTo).get()
                :
                null;
    }

    // getSeconds
    public long getSeconds(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
    }

    // getTimeAgo
    public String getTimeAgo(ArrayList<Payment> payments) {
        LocalDateTime minDate = getMinDate(payments);
        return minDate != null ? RelativeTime.getTimeAgo(getSeconds(minDate), activity) : "Incalculable";
    }

    // getTimeAgo
    public String getTimeAgo(String date) {
        if (date != null) {
            LocalDateTime localDateTime = parseToLocalDateTime(date);
            return localDateTime != null ? RelativeTime.getTimeAgo(getSeconds(localDateTime), activity) : activity.getString(R.string.incalculable);
        } else return null;
    }

    // getSumOfValues
    public double getSumOfValues(ArrayList<Payment> payments) {
        return payments.stream().filter(Objects::nonNull).mapToDouble(Payment::getValue).sum();
    }

    ////////////////////////
    // Keyboard utilities //
    ////////////////////////
    // KEYBOARD VARIABLES ///////////////////////////////////////////////
    // Number buttons                                                  //
    Button btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, btn0; //
    //                                                                 //
    // Letters buttons                                                 //
    Button btnQ, btnW, btnE, btnR, btnT, btnY, btnU, btnI, btnO, btnP; //
    Button btnA, btnS, btnD, btnF, btnG, btnH, btnJ, btnK, btnL, btnÑ; //
    Button btnZ, btnX, btnC, btnV, btnB, btnN, btnM;                   //
    Button btnSpace;                                                   //
    //                                                                 //
    // Action buttons                                                  //
    ImageView btnEnter, btnDelete;                                     //
    //                                                                 //
    // Divider                                                         //
    View dividerHorizontal;                                            //
    //                                                                 //
    // List of numbers & letters                                       //
    ArrayList<Button> btnNumbers, btnLetters;                          //
    /////////////////////////////////////////////////////////////////////

    public void customKeyBoard(int type, TextView secondTextView, boolean beforeComma, boolean whiteSpace, int maxLen, TextView textView, int resSelected, int resDeselected, Callable<Void> voidCallableOnDismiss) {
        // Initialize dialog
        keyboardDialog = buildDialog(keyboardDialog, keyboardView, R.style.KeyboardAnimation, R.style.AppTheme_Keyboard, true);

        // Update keyboard
        updateKeyboard(type, beforeComma, whiteSpace, maxLen, textView, secondTextView);

        keyboardDialog.setOnShowListener(dialog -> {
            // Set textView as selected
            textView.setBackgroundResource(resSelected);
        });

        keyboardDialog.setOnDismissListener(dialog -> {
            // Set textView as deselected
            textView.setBackgroundResource(resDeselected);

            if (voidCallableOnDismiss != null) {
                try {
                    voidCallableOnDismiss.call();
                } catch (Exception ignored) {
                    // Error dialog
                    __wait__("outside_the_system.json");
                }
            }
        });

        // Resize buttons
        ArrayList<Button> buttons = new ArrayList<>();
        buttons.addAll(btnNumbers);
        buttons.addAll(btnLetters.stream().filter(b -> b != btnSpace).collect(Collectors.toList()));
        SizeAdapter.changeHW(activity, buttons);
        ////////////////////////////////////////

        // Show dialog
        showDialog(keyboardDialog);
    }

    private void findKeysOfKeyboard() {
        /////////////
        // Numbers //
        /////////////
        {
            btnNumbers = new ArrayList<>();
            btn1 = keyboardView.findViewById(R.id.btn1);
            btnNumbers.add(btn1);
            btn2 = keyboardView.findViewById(R.id.btn2);
            btnNumbers.add(btn2);
            btn3 = keyboardView.findViewById(R.id.btn3);
            btnNumbers.add(btn3);
            btn4 = keyboardView.findViewById(R.id.btn4);
            btnNumbers.add(btn4);
            btn5 = keyboardView.findViewById(R.id.btn5);
            btnNumbers.add(btn5);
            btn6 = keyboardView.findViewById(R.id.btn6);
            btnNumbers.add(btn6);
            btn7 = keyboardView.findViewById(R.id.btn7);
            btnNumbers.add(btn7);
            btn8 = keyboardView.findViewById(R.id.btn8);
            btnNumbers.add(btn8);
            btn9 = keyboardView.findViewById(R.id.btn9);
            btnNumbers.add(btn9);
            btn0 = keyboardView.findViewById(R.id.btn0);
            btnNumbers.add(btn0);
        }

        /////////////
        // Letters //
        /////////////
        {
            btnLetters = new ArrayList<>();
            btnQ = keyboardView.findViewById(R.id.btnQ);
            btnLetters.add(btnQ);
            btnW = keyboardView.findViewById(R.id.btnW);
            btnLetters.add(btnW);
            btnE = keyboardView.findViewById(R.id.btnE);
            btnLetters.add(btnE);
            btnR = keyboardView.findViewById(R.id.btnR);
            btnLetters.add(btnR);
            btnT = keyboardView.findViewById(R.id.btnT);
            btnLetters.add(btnT);
            btnY = keyboardView.findViewById(R.id.btnY);
            btnLetters.add(btnY);
            btnU = keyboardView.findViewById(R.id.btnU);
            btnLetters.add(btnU);
            btnI = keyboardView.findViewById(R.id.btnI);
            btnLetters.add(btnI);
            btnO = keyboardView.findViewById(R.id.btnO);
            btnLetters.add(btnO);
            btnP = keyboardView.findViewById(R.id.btnP);
            btnLetters.add(btnP);

            btnA = keyboardView.findViewById(R.id.btnA);
            btnLetters.add(btnA);
            btnS = keyboardView.findViewById(R.id.btnS);
            btnLetters.add(btnS);
            btnD = keyboardView.findViewById(R.id.btnD);
            btnLetters.add(btnD);
            btnF = keyboardView.findViewById(R.id.btnF);
            btnLetters.add(btnF);
            btnG = keyboardView.findViewById(R.id.btnG);
            btnLetters.add(btnG);
            btnH = keyboardView.findViewById(R.id.btnH);
            btnLetters.add(btnH);
            btnJ = keyboardView.findViewById(R.id.btnJ);
            btnLetters.add(btnJ);
            btnK = keyboardView.findViewById(R.id.btnK);
            btnLetters.add(btnK);
            btnL = keyboardView.findViewById(R.id.btnL);
            btnLetters.add(btnL);
            btnÑ = keyboardView.findViewById(R.id.btnÑ);
            btnLetters.add(btnÑ);

            btnZ = keyboardView.findViewById(R.id.btnZ);
            btnLetters.add(btnZ);
            btnX = keyboardView.findViewById(R.id.btnX);
            btnLetters.add(btnX);
            btnC = keyboardView.findViewById(R.id.btnC);
            btnLetters.add(btnC);
            btnV = keyboardView.findViewById(R.id.btnV);
            btnLetters.add(btnV);
            btnB = keyboardView.findViewById(R.id.btnB);
            btnLetters.add(btnB);
            btnN = keyboardView.findViewById(R.id.btnN);
            btnLetters.add(btnN);
            btnM = keyboardView.findViewById(R.id.btnM);
            btnLetters.add(btnM);

            btnSpace = keyboardView.findViewById(R.id.btnSpace);
            btnLetters.add(btnSpace);
        }

        ////////////
        // Action //
        ////////////
        btnEnter = keyboardView.findViewById(R.id.btnEnter);
        btnDelete = keyboardView.findViewById(R.id.btnDelete);

        /////////////
        // Divider //
        /////////////
        dividerHorizontal = keyboardView.findViewById(R.id.dividerHorizontal);
    }

    private void letterKeyClickEvent(String string, int maxLen, TextView textView, TextView secondTextView) {
        String txt = textView.getText().toString();

        if (string.equals("_") && txt.length() == 0) return;

        if (len(txt) < maxLen) {
            if (txt.endsWith(" ")) {
                if (string.equals("_"))
                    return;
                else
                    string = string.toUpperCase();
            } else
                string = string.toLowerCase();
            txt += len(txt) == 0 || textView.isAllCaps() ? string.toUpperCase() : string;
            if (maxLen == 40)
                txt = txt.toLowerCase();
            textView.setText(txt.replace("_", " "));
            if (isNotNull(secondTextView))
                secondTextView.setText(txt.replace("_", " "));
        }
    }

    private void numberKeyClickEvent(String string, boolean isPhoneNumber, boolean beforeComma, int maxLen, TextView textView, TextView secondTextView) {
        String txt = textView.getText().toString().replace(".", "");

        if (isPhoneNumber) {
            if (maxLen == 11) { // Verification code
                if (txt.contains("_")) {
                    txt = txt.replaceFirst("_", string);
                    textView.setText(txt);
                    if (isNotNull(secondTextView))
                        secondTextView.setText(txt);
                }
            } else { // Phone number
                txt = txt.replace(" ", "");
                if (len(txt) < maxLen) {
                    txt += string;
                    textView.setText(txt);
                    if (isNotNull(secondTextView))
                        secondTextView.setText(txt);
                }
            }
        } else {
            if (beforeComma) {
                if (len(txt) < maxLen) {
                    if (txt.equals("0")) {
                        txt = string;
                    } else {
                        txt += string;
                    }
                    txt = df().format(Long.parseLong(txt)).replace(",", ".");
                    textView.setText(txt);
                    if (isNotNull(secondTextView))
                        secondTextView.setText(txt);
                }
            } else {
                if (txt.equals("00")) {
                    txt = "0" + string;
                    textView.setText(txt);
                    if (isNotNull(secondTextView))
                        secondTextView.setText(txt);
                } else if (txt.startsWith("0")) {
                    txt = txt.charAt(1) + string;
                    textView.setText(txt);
                    if (isNotNull(secondTextView))
                        secondTextView.setText(txt);
                }
            }
        }
    }

    private void updateKeyboard(int type, boolean beforeComma, boolean whiteSpace, int maxLen, TextView textView, TextView secondTextView) {
        if (type == 0) {
            for (Button b : btnNumbers) {
                b.setEnabled(true);
                b.setBackgroundResource(R.drawable.keyboard_key);
                b.setOnClickListener(v -> numberKeyClickEvent(b.getText().toString().toLowerCase(), whiteSpace, beforeComma, maxLen, textView, secondTextView));
            }

            for (Button b : btnLetters) {
                b.setEnabled(false);
                b.setBackgroundResource(R.drawable.keyboard_key_disabled);
                b.setOnClickListener(null);
            }

            btnSpace.setEnabled(false);
            btnSpace.setBackgroundResource(R.drawable.keyboard_key_disabled);
            btnSpace.setOnClickListener(null);
        } else if (type == 1) {
            for (Button b : btnNumbers) {
                b.setEnabled(false);
                b.setBackgroundResource(R.drawable.keyboard_key_disabled);
                b.setOnClickListener(null);
            }

            for (Button b : btnLetters) {
                b.setEnabled(true);
                b.setBackgroundResource(R.drawable.keyboard_key);
                b.setOnClickListener(v -> letterKeyClickEvent(b.getText().toString().toLowerCase(), maxLen, textView, secondTextView));
            }

            btnSpace.setEnabled(whiteSpace);
            btnSpace.setBackgroundResource(whiteSpace ? R.drawable.keyboard_key : R.drawable.keyboard_key_disabled);
        } else {
            for (Button b : btnNumbers) {
                b.setEnabled(true);
                b.setBackgroundResource(R.drawable.keyboard_key);
                b.setOnClickListener(v -> letterKeyClickEvent(b.getText().toString().toLowerCase(), maxLen, textView, secondTextView));
            }

            for (Button b : btnLetters) {
                b.setEnabled(true);
                b.setBackgroundResource(R.drawable.keyboard_key);
                b.setOnClickListener(v -> letterKeyClickEvent(b.getText().toString().toLowerCase(), maxLen, textView, secondTextView));
            }

            btnSpace.setEnabled(whiteSpace);
            btnSpace.setBackgroundResource(whiteSpace ? R.drawable.keyboard_key : R.drawable.keyboard_key_disabled);
        }

        if (isNotNull(secondTextView)) {
            secondTextView.setText(isNotNull(textView.getText().toString()) ? textView.getText().toString() : "");
            secondTextView.setVisibility(View.VISIBLE);
        } else {
            secondTextView.setText("");
            secondTextView.setVisibility(View.GONE);
        }

        btnEnter.setOnClickListener(v -> {
            if (len(textView.getText().toString()) > 0)
                dismissDialog(keyboardDialog);
        });

        btnDelete.setOnClickListener(v -> {
            String txt = textView.getText().toString().replace(".", "");

            if (type == 0) {
                if (whiteSpace) {
                    if (maxLen == 11) { // Verification code
                        int count = (int) txt.chars().filter(ch -> ch == '_').count();
                        if (count < 6) {
                            int pos = count == 0 ? txt.length() - 1 : txt.indexOf("_") - 2;
                            if (pos >= 0) {
                                txt = txt.substring(0, pos) + "_" + txt.substring(pos + 1);
                                textView.setText(txt);
                                if (isNotNull(secondTextView))
                                    secondTextView.setText(txt);
                            }
                        }
                    } else { // Phone number
                        if (len(txt) > 0) {
                            txt = txt.substring(0, len(txt) - 1);
                            textView.setText(txt);
                            if (isNotNull(secondTextView))
                                secondTextView.setText(txt);
                        }
                    }
                } else {
                    if (beforeComma) {
                        if (len(txt) == 1) {
                            textView.setText("0");
                            if (isNotNull(secondTextView))
                                secondTextView.setText("0");
                        } else {
                            txt = txt.substring(0, len(txt) - 1);
                            txt = df().format(Long.parseLong(txt)).replace(",", ".");
                            textView.setText(txt);
                            if (isNotNull(secondTextView))
                                secondTextView.setText(txt);
                        }
                    } else {
                        if (txt.equals("00") || txt.startsWith("0")) {
                            textView.setText("00");
                            if (isNotNull(secondTextView))
                                secondTextView.setText("00");
                        } else {
                            textView.setText("0" + txt.charAt(0));
                            if (isNotNull(secondTextView))
                                secondTextView.setText("0" + txt.charAt(0));
                        }
                    }
                }
            } else {
                if (len(txt) > 0) {
                    textView.setText(txt.substring(0, len(txt) - 1));
                    if (isNotNull(secondTextView))
                        secondTextView.setText(txt.substring(0, len(txt) - 1));
                }
            }
        });

        btnDelete.setOnLongClickListener(v -> {
            if (type == 0) {
                if (whiteSpace) {
                    if (maxLen == 11) { // Verification code
                        textView.setText("_ _ _ _ _ _");
                        if (isNotNull(secondTextView))
                            secondTextView.setText("_ _ _ _ _ _");
                    } else { // Phone number
                        textView.setText("");
                        if (isNotNull(secondTextView))
                            secondTextView.setText("");
                    }
                } else {
                    if (beforeComma) {
                        textView.setText("0");
                        if (isNotNull(secondTextView))
                            secondTextView.setText("0");
                    } else {
                        textView.setText("00");
                        if (isNotNull(secondTextView))
                            secondTextView.setText("00");
                    }
                }
            } else {
                textView.setText("");
                if (isNotNull(secondTextView))
                    secondTextView.setText("");
            }
            return true;
        });
    }

    // KEYBOARD CONTACTS VARIABLES ////////////////////////////////////////////////
    // Number buttons                                                            //
    Button btnc1, btnc2, btnc3, btnc4, btnc5, btnc6, btnc7, btnc8, btnc9, btnc0; //
    //                                                                           //
    // Letters buttons                                                           //
    Button btncQ, btncW, btncE, btncR, btncT, btncY, btncU, btncI, btncO, btncP; //
    Button btncA, btncS, btncD, btncF, btncG, btncH, btncJ, btncK, btncL, btncÑ; //
    Button btncZ, btncX, btncC, btncV, btncB, btncN, btncM;                      //
    Button btncSpace;                                                            //
    //                                                                           //
    // Action buttons                                                            //
    ImageView btncEnter, btncDelete;                                             //
    //                                                                           //
    // List of numbers & letters                                                 //
    ArrayList<Button> btncNumbers, btncLetters;                                  //
    //                                                                           //
    // Adapter                                                                   //
    ContactsAdapter contactsAdapter;                                             //
    //                                                                           //
    // Adapter fast                                                              //
    ContactsFastAdapter contactsFastAdapter;                                     //
    //                                                                           //
    // Adapter GP                                                                //
    ContactsGPAdapter contactsGPAdapter;                                         //
    ///////////////////////////////////////////////////////////////////////////////
    private void setEnabledAllContactsKeys(boolean enabled) {
        // Disable all keyboard buttons
        btncNumbers.forEach(btn -> btn.setEnabled(enabled));
        btncLetters.forEach(btn -> btn.setEnabled(enabled));
        btncEnter.setEnabled(enabled);
        btncDelete.setEnabled(enabled);
        ///////////////////////////////
    }

    // KEYBOARD CONTACTS ADAPTER //
    class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {

        Fragment fragment;
        Map<String, String> contacts = new HashMap<>();
        ArrayList<Friend> friends;
        String type;
        int pos;
        RecyclerView.Adapter adapter;
        TextView textView;
        RecyclerView recyclerView;
        RecyclerView.OnItemTouchListener itemTouchListener;


        BottomNavigationView bottomNavView;
        CustomViewPager viewPager;
        TabLayout tabLayout;

        // Constructor
        public ContactsAdapter(Fragment fragment, ArrayList<Friend> friends, String type, int pos, RecyclerView recyclerView, RecyclerView.Adapter adapter, RecyclerView.OnItemTouchListener itemTouchListener, TextView textView,
                               BottomNavigationView bottomNavView, CustomViewPager viewPager, TabLayout tabLayout) {
            this.fragment = fragment;
            this.friends = friends;
            this.type = type;
            this.pos = pos;
            this.recyclerView = recyclerView;
            this.adapter = adapter;
            this.itemTouchListener = itemTouchListener;
            this.textView = textView;
            this.bottomNavView = bottomNavView;
            this.viewPager = viewPager;
            this.tabLayout = tabLayout;
        }

        ///////////////////////
        // Adapter utilities //
        ///////////////////////
        // refreshContacts
        public void refreshContacts(Map<String, String> contacts) {
            this.contacts = contacts;
            notifyDataSetChanged();
        }

        ////////
        // UI //
        ////////
        RecyclerView.OnItemTouchListener disabler;

        // Disable UI
        private void disableUI() {
            // Initialize RecyclerViewDisabler
            if (disabler == null) disabler = new System.RecyclerViewDisabler();

            // Disable recyclerView scroll
            recyclerView.addOnItemTouchListener(disabler);

            // Disable contacts keyboard keys
            setEnabledAllContactsKeys(false);

            // Disable back
            ((TransactionsActivity) activity).back = false;
        }

        // Enable UI
        private void enableUI() {
            // Enable recyclerView scroll
            recyclerView.removeOnItemTouchListener(disabler);

            // Enabled contacts keyboard keys
            setEnabledAllContactsKeys(true);

            // Enable back
            ((TransactionsActivity) activity).back = true;
        }

        // try -> changeLookUpKey
        private void changeLookUpKey(int pos) {
            // Disable UI
            disableUI();

            // Try to connect to the internet
            tryToConnectToTheInternet(() -> {
                if (doesTheDatabaseExist() && !isDatabaseCorrupt()) letChangeLookUpKey(pos);
                else {
                    FATAL_ERROR = true;
                    activity.onBackPressed();
                }
                return null;
            });
        }

        // let -> changeLookUpKey
        private void letChangeLookUpKey(int pos) {
            // Initialize friend
            Friend friend = friends.get(this.pos);

            // Initialize old lookup key
            String oldLookUpKey = friend.getLookUpKey();

            // Initialize new lookup key
            String newLookUpKey = contacts.keySet().toArray()[pos].toString();

            // Set dialog non-cancelable
            keyboardContactsDialog.setCancelable(false);

            // Send request
            newRequest(config.isBm(), accountPos(), "U " + type + " " + oldLookUpKey + " " + newLookUpKey, () -> {
                // Connect to SQLite
                connect();

                // Execute request in SQLite
                write().execSQL("update movements set location = ? where accountId = ? and type = ? and location = ?", new String[]{newLookUpKey, def, type, oldLookUpKey});

                // Change lookUpKey
                friend.setLookUpKey(newLookUpKey);

                // Refresh groups?
                if (type.equals("HA") && friend.getPayments().stream().anyMatch(p -> p != null && p.getGroupId() != -1))
                    if (fragment.getParentFragment() != null) {
                        ((HaveFragment) fragment.getParentFragment()).haveGroupFragment.groupsAdapter.refreshList(getGroups(false));
                    }

                // Dismiss dialog
                dismissDialog(keyboardContactsDialog);

                // Enable UI
                enableUI();
                return null;
            });
        }

        // Set lookUpKey
        private void setLookUpKey(int pos) {
            // Data
            ((AddPaymentFragment) fragment).lookUpKey = contacts.keySet().toArray()[pos].toString();

            // Design
            ((AddPaymentFragment) fragment).txtvName.setText(findContactByLookupKey(contacts.keySet().toArray()[pos].toString()));

            // Dismiss dialog
            dismissDialog(keyboardContactsDialog);
        }

        @NonNull
        @Override
        public ContactsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.line_contact, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ContactsAdapter.ViewHolder holder, int position) {
            String key = contacts.keySet().toArray()[position].toString();
            String name = contacts.get(key);
            double value = 0;
            if (name.contains("  ")) {
                String strValue = name.split(" {2}")[1].replace(".", "").replace(",", ".");
                name = name.split(" {2}")[0];
                try {
                    value = Double.parseDouble(strValue);
                } catch (NumberFormatException ignored) {
                }
            }
            holder.btnContact.setText(name);
            if (value != 0) {
                holder.imgvTrending.setImageResource(value > 0 ? R.drawable.ic_trending_up_50 : R.drawable.ic_trending_down_50);
                holder.imgvTrending.setVisibility(View.VISIBLE);
                holder.txtvTrending.setText(System.moneyFormat.format(value));
                holder.txtvTrending.setTextColor(activity.getColor(value > 0 ? R.color.colorIncome : R.color.colorExpense));
                holder.txtvTrending.setVisibility(View.VISIBLE);
            } else {
                holder.imgvTrending.setVisibility(View.GONE);
                holder.txtvTrending.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return contacts != null ? contacts.size() : 0;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            Button btnContact;
            ImageView imgvTrending;
            TextView txtvTrending;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                // btnContact
                btnContact = itemView.findViewById(R.id.btnContact);
                btnContact.setOnClickListener(v -> {
                    if (fragment.isVisible()) {
                        if (fragment instanceof AddPaymentFragment)
                            setLookUpKey(getAdapterPosition());
                        else changeLookUpKey(getAdapterPosition());
                    } else {
                        // Change dialog dismiss listener
                        keyboardContactsDialog.setOnDismissListener(dialog -> toast(activity, WARNING_TOAST, activity.getString(R.string.It_has_not_been_possible_to_perform_this_action), Toast.LENGTH_SHORT));

                        // Dismiss dialog
                        dismissDialog(keyboardContactsDialog);
                    }
                });

                // imgvTrending
                imgvTrending = itemView.findViewById(R.id.imgvTrending);

                // txtvTrending
                txtvTrending = itemView.findViewById(R.id.txtvTrending);
            }
        }
    }

    private void findKeysOfKeyboardContacts() {
        /////////////
        // Numbers //
        /////////////
        {
            btncNumbers = new ArrayList<>();
            btnc1 = keyboardContactsView.findViewById(R.id.btn1);
            btncNumbers.add(btnc1);
            btnc2 = keyboardContactsView.findViewById(R.id.btn2);
            btncNumbers.add(btnc2);
            btnc3 = keyboardContactsView.findViewById(R.id.btn3);
            btncNumbers.add(btnc3);
            btnc4 = keyboardContactsView.findViewById(R.id.btn4);
            btncNumbers.add(btnc4);
            btnc5 = keyboardContactsView.findViewById(R.id.btn5);
            btncNumbers.add(btnc5);
            btnc6 = keyboardContactsView.findViewById(R.id.btn6);
            btncNumbers.add(btnc6);
            btnc7 = keyboardContactsView.findViewById(R.id.btn7);
            btncNumbers.add(btnc7);
            btnc8 = keyboardContactsView.findViewById(R.id.btn8);
            btncNumbers.add(btnc8);
            btnc9 = keyboardContactsView.findViewById(R.id.btn9);
            btncNumbers.add(btnc9);
            btnc0 = keyboardContactsView.findViewById(R.id.btn0);
            btncNumbers.add(btnc0);
        }

        /////////////
        // Letters //
        /////////////
        {
            btncLetters = new ArrayList<>();
            btncQ = keyboardContactsView.findViewById(R.id.btnQ);
            btncLetters.add(btncQ);
            btncW = keyboardContactsView.findViewById(R.id.btnW);
            btncLetters.add(btncW);
            btncE = keyboardContactsView.findViewById(R.id.btnE);
            btncLetters.add(btncE);
            btncR = keyboardContactsView.findViewById(R.id.btnR);
            btncLetters.add(btncR);
            btncT = keyboardContactsView.findViewById(R.id.btnT);
            btncLetters.add(btncT);
            btncY = keyboardContactsView.findViewById(R.id.btnY);
            btncLetters.add(btncY);
            btncU = keyboardContactsView.findViewById(R.id.btnU);
            btncLetters.add(btncU);
            btncI = keyboardContactsView.findViewById(R.id.btnI);
            btncLetters.add(btncI);
            btncO = keyboardContactsView.findViewById(R.id.btnO);
            btncLetters.add(btncO);
            btncP = keyboardContactsView.findViewById(R.id.btnP);
            btncLetters.add(btncP);

            btncA = keyboardContactsView.findViewById(R.id.btnA);
            btncLetters.add(btncA);
            btncS = keyboardContactsView.findViewById(R.id.btnS);
            btncLetters.add(btncS);
            btncD = keyboardContactsView.findViewById(R.id.btnD);
            btncLetters.add(btncD);
            btncF = keyboardContactsView.findViewById(R.id.btnF);
            btncLetters.add(btncF);
            btncG = keyboardContactsView.findViewById(R.id.btnG);
            btncLetters.add(btncG);
            btncH = keyboardContactsView.findViewById(R.id.btnH);
            btncLetters.add(btncH);
            btncJ = keyboardContactsView.findViewById(R.id.btnJ);
            btncLetters.add(btncJ);
            btncK = keyboardContactsView.findViewById(R.id.btnK);
            btncLetters.add(btncK);
            btncL = keyboardContactsView.findViewById(R.id.btnL);
            btncLetters.add(btncL);
            btncÑ = keyboardContactsView.findViewById(R.id.btnÑ);
            btncLetters.add(btncÑ);

            btncZ = keyboardContactsView.findViewById(R.id.btnZ);
            btncLetters.add(btncZ);
            btncX = keyboardContactsView.findViewById(R.id.btnX);
            btncLetters.add(btncX);
            btncC = keyboardContactsView.findViewById(R.id.btnC);
            btncLetters.add(btncC);
            btncV = keyboardContactsView.findViewById(R.id.btnV);
            btncLetters.add(btncV);
            btncB = keyboardContactsView.findViewById(R.id.btnB);
            btncLetters.add(btncB);
            btncN = keyboardContactsView.findViewById(R.id.btnN);
            btncLetters.add(btncN);
            btncM = keyboardContactsView.findViewById(R.id.btnM);
            btncLetters.add(btncM);

            btncSpace = keyboardContactsView.findViewById(R.id.btnSpace);
            btncLetters.add(btncSpace);
        }

        ////////////
        // Action //
        ////////////
        btncEnter = keyboardContactsView.findViewById(R.id.btnEnter);
        btncDelete = keyboardContactsView.findViewById(R.id.btnDelete);
    }

    public void customKeyBoardContacts(Fragment fragment, TextView secondTextView, TextView textView, int resSelected, int resDeselected, RecyclerView rvContacts, RecyclerView recyclerView, RecyclerView.Adapter adapter, String type, int pos, ArrayList<Friend> friends,
                                       BottomNavigationView bottomNavView, CustomViewPager viewPager, TabLayout tabLayout) {
        // Initialize dialog
        keyboardContactsDialog = buildDialog(keyboardContactsDialog, keyboardContactsView, R.style.KeyboardAnimation, R.style.AppTheme_Keyboard, true);

        // Initialize friend look up keys
        ArrayList<String> friendLookUpKeys = friends != null ? friends.stream().map(Friend::getLookUpKey).collect(Collectors.toCollection(ArrayList::new)) : null;

        // Initialize friend lookup key
        if (friends != null)
            if (friends.get(pos) != null) friendLookupKey = friends.get(pos).getLookUpKey();

        // Initialize RecyclerViewDisabler
        RecyclerView.OnItemTouchListener disabler = new RecyclerViewDisabler();

        // Disable UI
        disableUI(disabler, recyclerView, bottomNavView, viewPager, tabLayout);

        // Set same text to second textView
        secondTextView.setText(textView.getText().toString());

        // Initialize adapter
        contactsAdapter = new ContactsAdapter(fragment, friends, type, pos, rvContacts, adapter, disabler, textView, bottomNavView, viewPager, tabLayout);

        // Set adapter
        setAdapter(rvContacts, contactsAdapter, false, false, new LinearLayoutManager(activity, RecyclerView.VERTICAL, false));

        // Set data to RecyclerView
        contactsAdapter.refreshContacts(findContactsByName(textView.getText().toString(), friendLookUpKeys));

        // Update keyboard
        updateKeyboardContacts(50, textView, secondTextView, friendLookUpKeys);

        keyboardContactsDialog.setOnShowListener(dialog -> {
            // Change background of textView
            textView.setBackgroundResource(resSelected);
        });

        keyboardContactsDialog.setOnDismissListener(dialog -> {
            // Change background of textView
            textView.setBackgroundResource(resDeselected);

            if (!(fragment instanceof AddPaymentFragment) && !(fragment instanceof HaveGroupFragment)) {
                // Set dialog cancelable
                keyboardContactsDialog.setCancelable(true);

                // Notify change
                adapter.notifyItemChanged(pos);

                // Enable UI
                enableUI(disabler, recyclerView, bottomNavView, viewPager, tabLayout);
            }
        });

        // Resize buttons
        ArrayList<Button> buttons = new ArrayList<>();
        buttons.addAll(btncNumbers);
        buttons.addAll(btncLetters.stream().filter(b -> b != btncSpace).collect(Collectors.toList()));
        SizeAdapter.changeHW(activity, buttons);
        ////////////////////////////////////////

        // Show dialog
        showDialog(keyboardContactsDialog);
    }

    private void keyClickEvent(String string, int maxLen, TextView textView, TextView secondTextView, ArrayList<String> friendLookUpKeys) {
        String txt = textView.getText().toString();

        if (len(txt) < maxLen) {
            if (txt.endsWith(" ")) {
                if (string.equals("_"))
                    return;
                else
                    string = string.toUpperCase();
            } else
                string = string.toLowerCase();
            txt += len(txt) == 0 || textView.isAllCaps() ? string.toUpperCase() : string;
            textView.setText(txt.replace("_", " "));
            if (isNotNull(secondTextView))
                secondTextView.setText(txt.replace("_", " "));
        }

        contactsAdapter.refreshContacts(findContactsByName(txt, friendLookUpKeys));
    }

    private void updateKeyboardContacts(int maxLen, TextView textView, TextView secondTextView, ArrayList<String> friendLookUpKeys) {
        for (Button b : btncNumbers) {
            b.setEnabled(true);
            b.setBackgroundResource(R.drawable.keyboard_key);
            b.setOnClickListener(v -> keyClickEvent(b.getText().toString().toLowerCase(), maxLen, textView, secondTextView, friendLookUpKeys));
        }

        for (Button b : btncLetters) {
            b.setEnabled(true);
            b.setBackgroundResource(R.drawable.keyboard_key);
            b.setOnClickListener(v -> keyClickEvent(b.getText().toString().toLowerCase(), maxLen, textView, secondTextView, friendLookUpKeys));
        }

        btncSpace.setEnabled(true);
        btncSpace.setBackgroundResource(R.drawable.keyboard_key);

        btncEnter.setOnClickListener(v -> dismissDialog(keyboardContactsDialog));

        btncDelete.setOnClickListener(v -> {
            String txt = textView.getText().toString();

            if (len(txt) > 0) {
                textView.setText(txt.substring(0, len(txt) - 1));
                if (isNotNull(secondTextView))
                    secondTextView.setText(txt.substring(0, len(txt) - 1));
                contactsAdapter.refreshContacts(findContactsByName(txt.substring(0, len(txt) - 1), friendLookUpKeys));
            }
        });

        btncDelete.setOnLongClickListener(v -> {
            textView.setText("");
            if (isNotNull(secondTextView))
                secondTextView.setText("");
            contactsAdapter.refreshContacts(findContactsByName("", friendLookUpKeys));
            return false;
        });
    }

    // KEYBOARD CONTACTS FAST ADAPTER //
    class ContactsFastAdapter extends RecyclerView.Adapter<ContactsFastAdapter.ViewHolder> {

        Map<String, String> contacts = new HashMap<>();
        RecyclerView recyclerView;

        // Constructor
        public ContactsFastAdapter(RecyclerView recyclerView) {
            this.recyclerView = recyclerView;
        }

        ///////////////////////
        // Adapter utilities //
        ///////////////////////
        // refreshContacts
        public void refreshContacts(Map<String, String> contacts) {
            this.contacts = contacts;
            notifyDataSetChanged();
        }

        ////////
        // UI //
        ////////
        RecyclerView.OnItemTouchListener disabler;

        // Disable UI
        private void disableUI() {
            // Initialize RecyclerViewDisabler
            if (disabler == null) disabler = new System.RecyclerViewDisabler();

            // Disable recyclerView scroll
            recyclerView.addOnItemTouchListener(disabler);

            // Disable contacts keyboard keys
            setEnabledAllContactsKeys(false);

            // Disable back
            ((MainActivity) activity).back = false;
        }

        // Enable UI
        private void enableUI() {
            // Enable recyclerView scroll
            recyclerView.removeOnItemTouchListener(disabler);

            // Enabled contacts keyboard keys
            setEnabledAllContactsKeys(true);

            // Enable back
            ((MainActivity) activity).back = true;
        }

        // try -> addPayment
        private void addPayment(String lookUpKey) {
            // Disable UI
            disableUI();

            // Try to connect to the internet
            tryToConnectToTheInternet(() -> {
                if (doesTheDatabaseExist() && !isDatabaseCorrupt()) letAddPayment(lookUpKey);
                else { FATAL_ERROR = true; ((MainActivity) activity).back = true; start(); }
                return null;
            });
        }

        // let -> addPayment
        private void letAddPayment(String lookUpKey) {
            // Calculate type and value
            String type = calcSymbol == '+' ? "HA" : "SH";
            double value = calcSymbol == '+' ? newValue : -newValue;
            ////////////////////////////////////////////////////////

            // Set dialog non-cancelable
            keyboardContactsDialog.setCancelable(false);

            // Initialize now date
            String now = getLocalDateTimeNow();

            // Send request
            newRequest(config.isBm(), accountPos(), "I " + type + " " + now.replace(" ","_") + " " + lookUpKey + "," + value, () -> {
                // Connect to SQLite
                connect();

                // Execute request in SQLite
                write().execSQL("insert into movements (accountId, date, location, type, value) values(?,?,?,?,?)",
                        new String[]{def, now, lookUpKey, type, String.valueOf(value)});

                ///////////////
                // UI DESIGN //
                ///////////////
                // Change should or have && display
                if (type.equals("HA")) {
                    // Change have
                    account.setHave(account.getHave() + value);
                }
                else {
                    // Change should
                    account.setShould(account.getShould() + value);
                }

                // toast
                String name = contacts.get(lookUpKey);
                if (name.contains("  ")) name = name.split(" {2}")[0];
                toast(activity, value > 0 ? CONFIRMATION_TOAST : WARNING_TOAST, activity.getString(value > 0 ? R.string.NAME_new_payment_credit_of_VALUE : R.string.New_debt_to_NAME_of_VALUE)
                                                                                .replace("NAME", "\""+name+"\"")
                                                                                .replace("VALUE", moneyFormat.format(newValue)), Toast.LENGTH_SHORT);

                // Change values
                calcSymbol = '?';
                newValue = 0;
                newResult = 0;
                //////////////

                // Dismiss dialog
                dismissDialog(keyboardContactsDialog);

                // Enable UI
                enableUI();
                return null;
            });
        }

        @NonNull
        @Override
        public ContactsFastAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.line_contact, parent, false);
            return new ContactsFastAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ContactsFastAdapter.ViewHolder holder, int position) {
            String key = contacts.keySet().toArray()[position].toString();
            String name = contacts.get(key);
            double value = 0;
            if (name.contains("  ")) {
                String strValue = name.split(" {2}")[1].replace(".", "").replace(",", ".");
                name = name.split(" {2}")[0];
                try {
                    value = Double.parseDouble(strValue);
                } catch (NumberFormatException ignored) {
                }
            }
            holder.btnContact.setText(name);
            if (value != 0) {
                holder.imgvTrending.setImageResource(value > 0 ? R.drawable.ic_trending_up_50 : R.drawable.ic_trending_down_50);
                holder.imgvTrending.setVisibility(View.VISIBLE);
                holder.txtvTrending.setText(System.moneyFormat.format(value));
                holder.txtvTrending.setTextColor(activity.getColor(value > 0 ? R.color.colorIncome : R.color.colorExpense));
                holder.txtvTrending.setVisibility(View.VISIBLE);
            } else {
                holder.imgvTrending.setVisibility(View.GONE);
                holder.txtvTrending.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return contacts != null ? contacts.size() : 0;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            Button btnContact;
            ImageView imgvTrending;
            TextView txtvTrending;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                // btnContact
                btnContact = itemView.findViewById(R.id.btnContact);
                btnContact.setOnClickListener(v -> {
                    if (activity instanceof MainActivity) {
                        if (newValue > 0 && calcSymbol != '?') addPayment(contacts.keySet().toArray()[getAdapterPosition()].toString());
                    } else {
                        // Change dialog dismiss listener
                        keyboardContactsDialog.setOnDismissListener(dialog -> toast(activity, WARNING_TOAST, activity.getString(R.string.It_has_not_been_possible_to_perform_this_action), Toast.LENGTH_SHORT));

                        // Dismiss dialog
                        dismissDialog(keyboardContactsDialog);
                    }
                });

                // imgvTrending
                imgvTrending = itemView.findViewById(R.id.imgvTrending);

                // txtvTrending
                txtvTrending = itemView.findViewById(R.id.txtvTrending);
            }
        }
    }

    @SuppressLint("SetTextI18n")
    public void customKeyBoardContactsFast(TextView secondTextView, TextView textView, int resSelected, int resDeselected, RecyclerView rvContacts) {
        // Initialize dialog
        keyboardContactsDialog = buildDialog(keyboardContactsDialog, keyboardContactsView, R.style.KeyboardAnimation, R.style.AppTheme_Keyboard, true);

        // Initialize friend look up keys
        ArrayList<String> friendLookUpKeys = null;

        // Set empty text
        secondTextView.setText("");

        // Initialize adapter
        contactsFastAdapter = new ContactsFastAdapter(rvContacts);

        // Set adapter
        setAdapter(rvContacts, contactsFastAdapter, false, false, new LinearLayoutManager(activity, RecyclerView.VERTICAL, false));

        // Set data to RecyclerView
        contactsFastAdapter.refreshContacts(findContactsByName("", friendLookUpKeys));

        // Update keyboard
        updateKeyboardContactsFast(50, textView, secondTextView, friendLookUpKeys);

        keyboardContactsDialog.setOnShowListener(dialog -> {
            // Change background of textView
            textView.setBackgroundResource(resSelected);
        });

        keyboardContactsDialog.setOnDismissListener(dialog -> {
            // Change background of textView
            textView.setBackgroundResource(resDeselected);

            if (activity instanceof MainActivity) {
                // Set dialog cancelable
                keyboardContactsDialog.setCancelable(true);

                // Values
                ((MainActivity)activity).txtvCalc.setText(moneyFormat.format(account.getMoney()) + " " + calcSymbol + " " +
                        (newValue != 0 ? moneyFormat.format(newValue) : "?"));

                if (calcSymbol != '?')
                    newResult = calcSymbol == '+' ? account.getMoney() + newValue : account.getMoney() - newValue;
                ((MainActivity)activity).txtvResult.setText("= " + (newResult != 0.00 ? moneyFormat.format(newResult) : '?'));

                // Enable point
                pointEnabled = true;

                // Enable UI
                enableUI();

                // txtName invisible
                ((MainActivity)activity).txtvName.setVisibility(View.INVISIBLE);

                // txtResult visible
                ((MainActivity)activity).txtvResult.setVisibility(View.VISIBLE);
            }
        });

        // Resize buttons
        ArrayList<Button> buttons = new ArrayList<>();
        buttons.addAll(btncNumbers);
        buttons.addAll(btncLetters.stream().filter(b -> b != btncSpace).collect(Collectors.toList()));
        SizeAdapter.changeHW(activity, buttons);
        ////////////////////////////////////////

        // Show dialog
        showDialog(keyboardContactsDialog);
    }

    private void keyClickEventFast(String string, int maxLen, TextView textView, TextView secondTextView, ArrayList<String> friendLookUpKeys) {
        String txt = textView.getText().toString();

        if (len(txt) < maxLen) {
            if (txt.endsWith(" ")) {
                if (string.equals("_"))
                    return;
                else
                    string = string.toUpperCase();
            } else
                string = string.toLowerCase();
            txt += len(txt) == 0 || textView.isAllCaps() ? string.toUpperCase() : string;
            textView.setText(txt.replace("_", " "));
            if (isNotNull(secondTextView))
                secondTextView.setText(txt.replace("_", " "));
        }

        contactsFastAdapter.refreshContacts(findContactsByName(txt, friendLookUpKeys));
    }

    private void updateKeyboardContactsFast(int maxLen, TextView textView, TextView secondTextView, ArrayList<String> friendLookUpKeys) {
        for (Button b : btncNumbers) {
            b.setEnabled(true);
            b.setBackgroundResource(R.drawable.keyboard_key);
            b.setOnClickListener(v -> keyClickEventFast(b.getText().toString().toLowerCase(), maxLen, textView, secondTextView, friendLookUpKeys));
        }

        for (Button b : btncLetters) {
            b.setEnabled(true);
            b.setBackgroundResource(R.drawable.keyboard_key);
            b.setOnClickListener(v -> keyClickEventFast(b.getText().toString().toLowerCase(), maxLen, textView, secondTextView, friendLookUpKeys));
        }

        btncSpace.setEnabled(true);
        btncSpace.setBackgroundResource(R.drawable.keyboard_key);

        btncEnter.setOnClickListener(v -> dismissDialog(keyboardContactsDialog));

        btncDelete.setOnClickListener(v -> {
            String txt = textView.getText().toString();

            if (len(txt) > 0) {
                textView.setText(txt.substring(0, len(txt) - 1));
                if (isNotNull(secondTextView))
                    secondTextView.setText(txt.substring(0, len(txt) - 1));
                contactsFastAdapter.refreshContacts(findContactsByName(txt.substring(0, len(txt) - 1), friendLookUpKeys));
            }
        });

        btncDelete.setOnLongClickListener(v -> {
            textView.setText("");
            if (isNotNull(secondTextView))
                secondTextView.setText("");
            contactsFastAdapter.refreshContacts(findContactsByName("", friendLookUpKeys));
            return false;
        });
    }

    // KEYBOARD CONTACTS GP ADAPTER //
    class ContactsGPAdapter extends RecyclerView.Adapter<ContactsGPAdapter.ViewHolder> {

        Fragment fragment;
        int pos;
        FriendsOfGroupAdapter adapter;
        Map<String, String> contacts = new HashMap<>();

        HashMap<String, String> map;
        public ArrayList<String> lookUpKeys;

        // Constructor
        public ContactsGPAdapter(Fragment fragment, int pos, FriendsOfGroupAdapter adapter, HashMap<String, String> map) {
            this.fragment = fragment;
            this.pos = pos;
            this.adapter = adapter;
            this.map = map;
        }

        public ContactsGPAdapter(Fragment fragment, int pos, FriendsOfGroupAdapter adapter, ArrayList<String> lookUpKeys) {
            this.fragment = fragment;
            this.pos = pos;
            this.adapter = adapter;
            this.lookUpKeys = lookUpKeys;
        }

        ///////////////////////
        // Adapter utilities //
        ///////////////////////
        // refreshContacts
        public void refreshContacts(Map<String, String> contacts) {
            this.contacts = contacts;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ContactsGPAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.line_contact, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ContactsGPAdapter.ViewHolder holder, int position) {
            String key = contacts.keySet().toArray()[position].toString();
            String name = contacts.get(key);
            double value = 0;
            if (name.contains("  ")) {
                String strValue = name.split(" {2}")[1].replace(".", "").replace(",", ".");
                name = name.split(" {2}")[0];
                try {
                    value = Double.parseDouble(strValue);
                } catch (NumberFormatException ignored) {
                }
            }
            holder.btnContact.setText(name);
            if (value != 0) {
                holder.imgvTrending.setImageResource(value > 0 ? R.drawable.ic_trending_up_50 : R.drawable.ic_trending_down_50);
                holder.imgvTrending.setVisibility(View.VISIBLE);
                holder.txtvTrending.setText(System.moneyFormat.format(value));
                holder.txtvTrending.setTextColor(activity.getColor(value > 0 ? R.color.colorIncome : R.color.colorExpense));
                holder.txtvTrending.setVisibility(View.VISIBLE);
            } else {
                holder.imgvTrending.setVisibility(View.GONE);
                holder.txtvTrending.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return contacts != null ? contacts.size() : 0;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            Button btnContact;
            ImageView imgvTrending;
            TextView txtvTrending;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                // btnContact
                btnContact = itemView.findViewById(R.id.btnContact);
                btnContact.setOnClickListener(v -> {
                    if (fragment.isVisible()) {
                        adapter.next = true;
                        if (lookUpKeys != null)
                            lookUpKeys.set(pos, contacts.keySet().toArray()[getAdapterPosition()].toString());
                        else
                            map.put(map.keySet().toArray()[pos].toString(), contacts.keySet().toArray()[getAdapterPosition()].toString());
                        dismissDialog(keyboardContactsDialog);
                    } else {
                        // Change dialog dismiss listener
                        keyboardContactsDialog.setOnDismissListener(dialog -> toast(activity, WARNING_TOAST, activity.getString(R.string.It_has_not_been_possible_to_perform_this_action), Toast.LENGTH_SHORT));

                        // Dismiss dialog
                        dismissDialog(keyboardContactsDialog);
                    }
                });

                // imgvTrending
                imgvTrending = itemView.findViewById(R.id.imgvTrending);

                // txtvTrending
                txtvTrending = itemView.findViewById(R.id.txtvTrending);
            }
        }
    }

    public void customKeyBoardContactsGP(Fragment fragment, TextView secondTextView, TextView textView, int resSelected, int resDeselected, RecyclerView rvContacts, RecyclerView recyclerView, FriendsOfGroupAdapter adapter, int pos) {
        // Initialize map
        HashMap<String, String> map = adapter.map;

        // Initialize lookUpKeys
        ArrayList<String> lookUpKeys = adapter.lookUpKeys;

        // Initialize dialog
        /*keyboardContactsDialog = buildDialog(keyboardContactsDialog, keyboardContactsView, R.style.KeyboardAnimation, R.style.AppTheme_Keyboard,
                adapter.names ?
                        pos > 0 && pos < map.size()
                        :
                        pos > 0 && pos < lookUpKeys.size());*/

        keyboardContactsDialog = buildDialog(keyboardContactsDialog, keyboardContactsView, R.style.KeyboardAnimation, R.style.AppTheme_Keyboard, true);


        // Set same text to second textView
        secondTextView.setText(textView.getText().toString());

        // Initialize adapter
        contactsGPAdapter = adapter.names ? new ContactsGPAdapter(fragment, pos, adapter, map) : new ContactsGPAdapter(fragment, pos, adapter, lookUpKeys);

        // Set adapter
        setAdapter(rvContacts, contactsGPAdapter, false, false, new LinearLayoutManager(activity, RecyclerView.VERTICAL, false));

        // Set data to RecyclerView
        contactsGPAdapter.refreshContacts(findContactsByName(textView.getText().toString(),
                adapter.names ?
                        map.values().stream().filter(Objects::nonNull).filter(k -> !VoiceAssistant.isName(k)).collect(Collectors.toCollection(ArrayList::new))
                        :
                        lookUpKeys.stream().filter(Objects::nonNull).collect(Collectors.toCollection(ArrayList::new))));

        // Update keyboard
        updateKeyboardContactsGP(50, textView, secondTextView,
                adapter.names ?
                        map.values().stream().filter(Objects::nonNull).filter(k -> !VoiceAssistant.isName(k)).collect(Collectors.toCollection(ArrayList::new))
                        :
                        lookUpKeys.stream().filter(Objects::nonNull).collect(Collectors.toCollection(ArrayList::new))
                , adapter, pos);


        keyboardContactsDialog.setOnShowListener(dialog -> {
            // Change background of textView
            textView.setBackgroundResource(resSelected);
        });

        keyboardContactsDialog.setOnDismissListener(dialog -> {

            if (pos == 0 && !adapter.next) {
                // *This will fix a bug
                HaveGroupFragment.hasNewGroup = true;

                // Close
                ((HaveGroupFragment) fragment).closeSpeech();
                return;
            }

            // Change background of textView
            textView.setBackgroundResource(resDeselected);

            // Change actual adapter pos and notify
            adapter.notifyItemChanged(pos);
            if (adapter.next) adapter.pos++;
            else adapter.pos--;
            ///////////////////

            // Initialize size
            int size = adapter.names ? map.size() : lookUpKeys.size();

            if (adapter.pos == size) {
                // New item to set group name and notify
                if (adapter.names) {
                    adapter.map = null;
                    adapter.lookUpKeys = new ArrayList<>(map.values());
                    adapter.names = false;
                }
                adapter.lookUpKeys.add(".");
                adapter.notifyDataSetChanged();
                ///////////////////////////////

                // Smooth scroll with delay
                new Handler().postDelayed(() -> recyclerView.smoothScrollToPosition(adapter.pos), 500);
            } else {
                // Smooth scroll with delay
                new Handler().postDelayed(() -> recyclerView.smoothScrollToPosition(adapter.pos), 500);
            }
        });

        // Resize buttons
        ArrayList<Button> buttons = new ArrayList<>();
        buttons.addAll(btncNumbers);
        buttons.addAll(btncLetters.stream().filter(b -> b != btncSpace).collect(Collectors.toList()));
        SizeAdapter.changeHW(activity, buttons);
        ////////////////////////////////////////

        // Show dialog
        showDialog(keyboardContactsDialog);
    }

    private void keyClickEventGP(String string, int maxLen, TextView textView, TextView secondTextView, ArrayList<String> friendLookUpKeys) {
        String txt = textView.getText().toString();

        if (len(txt) < maxLen) {
            if (txt.endsWith(" ")) {
                if (string.equals("_"))
                    return;
                else
                    string = string.toUpperCase();
            } else
                string = string.toLowerCase();
            txt += len(txt) == 0 || textView.isAllCaps() ? string.toUpperCase() : string;
            textView.setText(txt.replace("_", " "));
            if (isNotNull(secondTextView))
                secondTextView.setText(txt.replace("_", " "));
        }

        contactsGPAdapter.refreshContacts(findContactsByName(txt, friendLookUpKeys));
    }

    private void updateKeyboardContactsGP(int maxLen, TextView textView, TextView secondTextView, ArrayList<String> friendLookUpKeys, FriendsOfGroupAdapter adapter, int pos) {
        for (Button b : btncNumbers) {
            b.setEnabled(true);
            b.setBackgroundResource(R.drawable.keyboard_key);
            b.setOnClickListener(v -> keyClickEventGP(b.getText().toString().toLowerCase(), maxLen, textView, secondTextView, friendLookUpKeys));
        }

        for (Button b : btncLetters) {
            b.setEnabled(true);
            b.setBackgroundResource(R.drawable.keyboard_key);
            b.setOnClickListener(v -> keyClickEventGP(b.getText().toString().toLowerCase(), maxLen, textView, secondTextView, friendLookUpKeys));
        }

        btncSpace.setEnabled(true);
        btncSpace.setBackgroundResource(R.drawable.keyboard_key);

        btncEnter.setOnClickListener(v -> {

            if (adapter.names) {
                // Initialize map
                HashMap<String, String> map = adapter.map;

                if (map.size() > pos + 1 && map.get(map.keySet().toArray()[pos].toString()) != null) {
                    adapter.next = true;
                    dismissDialog(keyboardContactsDialog);
                }
            } else {
                // Initialize arrayList
                ArrayList<String> arrayList = adapter.lookUpKeys;

                if (arrayList.size() > pos + 1 && arrayList.get(pos) != null) {
                    adapter.next = true;
                    dismissDialog(keyboardContactsDialog);
                }
            }
        });

        btncDelete.setOnClickListener(v -> {
            String txt = textView.getText().toString();

            if (len(txt) > 0) {
                textView.setText(txt.substring(0, len(txt) - 1));
                if (isNotNull(secondTextView))
                    secondTextView.setText(txt.substring(0, len(txt) - 1));
                contactsGPAdapter.refreshContacts(findContactsByName(txt.substring(0, len(txt) - 1), friendLookUpKeys));
            }
        });

        btncDelete.setOnLongClickListener(v -> {
            textView.setText("");
            if (isNotNull(secondTextView))
                secondTextView.setText("");
            contactsGPAdapter.refreshContacts(findContactsByName("", friendLookUpKeys));
            return false;
        });
    }

    // KEYBOARD CONCEPTS VARIABLES //////////////////////////////////////////////////////////
    // Number buttons                                                                      //
    Button btnct1, btnct2, btnct3, btnct4, btnct5, btnct6, btnct7, btnct8, btnct9, btnct0; //
    //                                                                                     //
    // Letters buttons                                                                     //
    Button btnctQ, btnctW, btnctE, btnctR, btnctT, btnctY, btnctU, btnctI, btnctO, btnctP; //
    Button btnctA, btnctS, btnctD, btnctF, btnctG, btnctH, btnctJ, btnctK, btnctL, btnctÑ; //
    Button btnctZ, btnctX, btnctC, btnctV, btnctB, btnctN, btnctM;                         //
    Button btnctSpace;                                                                     //
    //                                                                                     //
    // Action buttons                                                                      //
    ImageView btnctEnter, btnctDelete;                                                     //
    //                                                                                     //
    // List of numbers & letters                                                           //
    ArrayList<Button> btnctNumbers, btnctLetters;                                          //
    //                                                                                     //
    // Adapter                                                                             //
    ConceptsAdapter conceptsAdapter;                                                       //
    // ArrayList                                                                           //
    ArrayList<Concept> concepts;                                                           //
    /////////////////////////////////////////////////////////////////////////////////////////

    private void setEnabledAllConceptsKeys(boolean enabled) {
        // Disable all keyboard buttons
        btnctNumbers.forEach(btn -> btn.setEnabled(enabled));
        btnctLetters.forEach(btn -> btn.setEnabled(enabled));
        btnctEnter.setEnabled(enabled);
        btnctDelete.setEnabled(enabled);
        ////////////////////////////////
    }

    // KEYBOARD CONCEPTS ADAPTER //
    class ConceptsAdapter extends RecyclerView.Adapter<ConceptsAdapter.ViewHolder> {

        ConceptFragment fragment;
        RecyclerView recyclerView;
        ArrayList<Concept> concepts;

        public ConceptsAdapter(ConceptFragment fragment, RecyclerView recyclerView) {
            this.fragment = fragment;
            this.recyclerView = recyclerView;
        }

        ///////////////////////
        // Adapter utilities //
        ///////////////////////
        // refreshConcepts
        public void refreshConcepts(ArrayList<Concept> concepts) {
            this.concepts = concepts;
            notifyDataSetChanged();
        }

        // try -> deleteConcept
        private void deleteConcept(Concept concept) {
            // Disable back
            ((ConCatActivity) activity).back = false;

            // Disable keys
            setEnabledAllConceptsKeys(false);

            // Set dialog non-cancelable
            keyboardConceptsDialog.setCancelable(false);

            // Initialize disabler
            RecyclerViewDisabler disabler = new RecyclerViewDisabler();

            // Disable UI
            disableUI(disabler, recyclerView, null, null, null);

            // Try to connect to the internet
            tryToConnectToTheInternet(() -> {
                if (doesTheDatabaseExist() && !isDatabaseCorrupt())
                    letDeleteConcept(concept, disabler);
                else {
                    FATAL_ERROR = true;
                    activity.onBackPressed();
                }
                return null;
            });
        }

        // let -> deleteConcept
        private void letDeleteConcept(Concept concept, RecyclerViewDisabler disabler) {
            // Send request
            newRequest(config.isBm(), accountPos(), "D C " + concept.getId(), () -> {
                // Connect to SQLite
                connect();

                // Execute request in SQLite
                write().execSQL("delete from movements where conceptId = ? and type in('I','E')", new String[]{String.valueOf(concept.getId())});
                write().execSQL("delete from concepts where id = ?", new String[]{String.valueOf(concept.getId())});
                /////////////////////////////////////////////////////////////////////////////////////////////////////////

                // Month benefits and expenses
                sumMonthBenefits();
                sumMonthExpenses();
                ///////////////////

                ///////////////
                // UI DESIGN //
                ///////////////
                // Refresh list
                System.this.concepts = findConceptsByName(conceptsAdapter.fragment.txtvConcept.getText().toString(), getConcepts());
                conceptsAdapter.concepts = System.this.concepts;
                ////////////////////////////////////////////////

                // Notify
                conceptsAdapter.notifyDataSetChanged();

                // Enable keys
                setEnabledAllConceptsKeys(true);

                // Set dialog cancelable
                keyboardConceptsDialog.setCancelable(true);

                // Enable UI
                enableUI(disabler, recyclerView, null, null, null);

                // Enable back
                ((ConCatActivity) activity).back = true;
                return null;
            });
        }

        // try -> addMovement -> existing or non-existing concept
        public void addMovement(String name, boolean doesTheConceptExist) {
            // Disable back
            ((ConCatActivity) activity).back = false;

            // Disable all keyboard buttons
            setEnabledAllConceptsKeys(false);

            // Set dialog non-cancelable
            keyboardConceptsDialog.setCancelable(false);

            // Disable UI
            disableUI(new RecyclerViewDisabler(), recyclerView, null, null, null);

            // Try to connect to the internet
            tryToConnectToTheInternet(() -> {
                if (doesTheDatabaseExist() && !isDatabaseCorrupt())
                    letAddMovement(name, doesTheConceptExist);
                else {
                    FATAL_ERROR = true;
                    fragment.system.activity.onBackPressed();
                }
                return null;
            });
        }

        // let -> addMovement
        @SuppressLint("Recycle")
        private void letAddMovement(String name, boolean doesTheConceptExist) {
            // Concept as null
            Concept concept = null;

            // Does the concept exist?
            if (doesTheConceptExist)
                concept = concepts.stream().filter(c -> c.getName().toLowerCase().equals(name.toLowerCase())).findFirst().get();

            // Insert movement
            insertMovement(concept, name, fragment.txtvConcept.getText().toString());
        }

        @NonNull
        @Override
        public ConceptsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.line_concept, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ConceptsAdapter.ViewHolder holder, int position) {
            Concept concept = concepts.get(position);
            holder.btnConcept.setText(concept.getName());
            if (concept.getMoney() != 0) {
                holder.imgvTrending.setImageResource(concept.getMoney() > 0 ? R.drawable.ic_trending_up_50 : R.drawable.ic_trending_down_50);
                holder.imgvTrending.setVisibility(View.VISIBLE);
                holder.txtvTrending.setText(System.moneyFormat.format(concept.getMoney()));
                holder.txtvTrending.setTextColor(activity.getColor(concept.getMoney() > 0 ? R.color.colorIncome : R.color.colorExpense));
                holder.txtvTrending.setVisibility(View.VISIBLE);
            } else {
                holder.imgvTrending.setVisibility(View.GONE);
                holder.txtvTrending.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return concepts != null ? concepts.size() : 0;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView btnDelete;
            Button btnConcept;
            ImageView imgvTrending;
            TextView txtvTrending;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                // btnDelete
                btnDelete = itemView.findViewById(R.id.btnDelete);
                btnDelete.setOnClickListener(v -> {
                    if (fragment.isVisible() && getAdapterPosition() > -1 && getAdapterPosition() < concepts.size())
                        deleteConcept(concepts.get(getAdapterPosition()));
                    else {
                        // Change dialog dismiss listener
                        keyboardContactsDialog.setOnDismissListener(dialog -> toast(activity, WARNING_TOAST, activity.getString(R.string.It_has_not_been_possible_to_perform_this_action), Toast.LENGTH_SHORT));

                        // Dismiss dialog
                        dismissDialog(keyboardContactsDialog);
                    }
                });
                // btnConcept
                btnConcept = itemView.findViewById(R.id.btnConcept);
                btnConcept.setOnClickListener(v -> {
                    if (fragment.isVisible() && getAdapterPosition() > -1 && getAdapterPosition() < concepts.size())
                        addMovement(concepts.get(getAdapterPosition()).getName(), true);
                    else {
                        // Change dialog dismiss listener
                        keyboardContactsDialog.setOnDismissListener(dialog -> toast(activity, WARNING_TOAST, activity.getString(R.string.It_has_not_been_possible_to_perform_this_action), Toast.LENGTH_SHORT));

                        // Dismiss dialog
                        dismissDialog(keyboardContactsDialog);
                    }
                });

                // imgvTrending
                imgvTrending = itemView.findViewById(R.id.imgvTrending);

                // txtvTrending
                txtvTrending = itemView.findViewById(R.id.txtvTrending);
            }
        }
    }

    private void findKeysOfKeyboardConcepts() {
        /////////////
        // Numbers //
        /////////////
        {
            btnctNumbers = new ArrayList<>();
            btnct1 = keyboardConceptsView.findViewById(R.id.btn1);
            btnctNumbers.add(btnct1);
            btnct2 = keyboardConceptsView.findViewById(R.id.btn2);
            btnctNumbers.add(btnct2);
            btnct3 = keyboardConceptsView.findViewById(R.id.btn3);
            btnctNumbers.add(btnct3);
            btnct4 = keyboardConceptsView.findViewById(R.id.btn4);
            btnctNumbers.add(btnct4);
            btnct5 = keyboardConceptsView.findViewById(R.id.btn5);
            btnctNumbers.add(btnct5);
            btnct6 = keyboardConceptsView.findViewById(R.id.btn6);
            btnctNumbers.add(btnct6);
            btnct7 = keyboardConceptsView.findViewById(R.id.btn7);
            btnctNumbers.add(btnct7);
            btnct8 = keyboardConceptsView.findViewById(R.id.btn8);
            btnctNumbers.add(btnct8);
            btnct9 = keyboardConceptsView.findViewById(R.id.btn9);
            btnctNumbers.add(btnct9);
            btnct0 = keyboardConceptsView.findViewById(R.id.btn0);
            btnctNumbers.add(btnct0);
        }

        /////////////
        // Letters //
        /////////////
        {
            btnctLetters = new ArrayList<>();
            btnctQ = keyboardConceptsView.findViewById(R.id.btnQ);
            btnctLetters.add(btnctQ);
            btnctW = keyboardConceptsView.findViewById(R.id.btnW);
            btnctLetters.add(btnctW);
            btnctE = keyboardConceptsView.findViewById(R.id.btnE);
            btnctLetters.add(btnctE);
            btnctR = keyboardConceptsView.findViewById(R.id.btnR);
            btnctLetters.add(btnctR);
            btnctT = keyboardConceptsView.findViewById(R.id.btnT);
            btnctLetters.add(btnctT);
            btnctY = keyboardConceptsView.findViewById(R.id.btnY);
            btnctLetters.add(btnctY);
            btnctU = keyboardConceptsView.findViewById(R.id.btnU);
            btnctLetters.add(btnctU);
            btnctI = keyboardConceptsView.findViewById(R.id.btnI);
            btnctLetters.add(btnctI);
            btnctO = keyboardConceptsView.findViewById(R.id.btnO);
            btnctLetters.add(btnctO);
            btnctP = keyboardConceptsView.findViewById(R.id.btnP);
            btnctLetters.add(btnctP);

            btnctA = keyboardConceptsView.findViewById(R.id.btnA);
            btnctLetters.add(btnctA);
            btnctS = keyboardConceptsView.findViewById(R.id.btnS);
            btnctLetters.add(btnctS);
            btnctD = keyboardConceptsView.findViewById(R.id.btnD);
            btnctLetters.add(btnctD);
            btnctF = keyboardConceptsView.findViewById(R.id.btnF);
            btnctLetters.add(btnctF);
            btnctG = keyboardConceptsView.findViewById(R.id.btnG);
            btnctLetters.add(btnctG);
            btnctH = keyboardConceptsView.findViewById(R.id.btnH);
            btnctLetters.add(btnctH);
            btnctJ = keyboardConceptsView.findViewById(R.id.btnJ);
            btnctLetters.add(btnctJ);
            btnctK = keyboardConceptsView.findViewById(R.id.btnK);
            btnctLetters.add(btnctK);
            btnctL = keyboardConceptsView.findViewById(R.id.btnL);
            btnctLetters.add(btnctL);
            btnctÑ = keyboardConceptsView.findViewById(R.id.btnÑ);
            btnctLetters.add(btnctÑ);

            btnctZ = keyboardConceptsView.findViewById(R.id.btnZ);
            btnctLetters.add(btnctZ);
            btnctX = keyboardConceptsView.findViewById(R.id.btnX);
            btnctLetters.add(btnctX);
            btnctC = keyboardConceptsView.findViewById(R.id.btnC);
            btnctLetters.add(btnctC);
            btnctV = keyboardConceptsView.findViewById(R.id.btnV);
            btnctLetters.add(btnctV);
            btnctB = keyboardConceptsView.findViewById(R.id.btnB);
            btnctLetters.add(btnctB);
            btnctN = keyboardConceptsView.findViewById(R.id.btnN);
            btnctLetters.add(btnctN);
            btnctM = keyboardConceptsView.findViewById(R.id.btnM);
            btnctLetters.add(btnctM);

            btnctSpace = keyboardConceptsView.findViewById(R.id.btnSpace);
            btnctLetters.add(btnctSpace);
        }

        ////////////
        // Action //
        ////////////
        btnctEnter = keyboardConceptsView.findViewById(R.id.btnEnter);
        btnctDelete = keyboardConceptsView.findViewById(R.id.btnDelete);
    }

    public void customKeyBoardConcepts(ConceptFragment fragment) {
        // Initialize dialog
        keyboardConceptsDialog = buildDialog(keyboardConceptsDialog, keyboardConceptsView, R.style.KeyboardAnimation, R.style.AppTheme_Keyboard, true);

        // Get textViews
        TextView textView = fragment.txtvConcept;
        TextView secondTextView = keyboardConceptsView.findViewById(R.id.textView);
        ///////////////////////////////////////////////////////////////////////////

        // Find recyclerView
        RecyclerView recyclerView = keyboardConceptsView.findViewById(R.id.rvConcepts);

        // Initialize adapter
        conceptsAdapter = new ConceptsAdapter(fragment, recyclerView);

        // Set adapter
        setAdapter(recyclerView, conceptsAdapter, false, false, new LinearLayoutManager(activity, RecyclerView.VERTICAL, false));

        // Initialize concepts
        concepts = getConcepts();

        // Set data to RecyclerView
        conceptsAdapter.refreshConcepts(findConceptsByName(textView.getText().toString(), concepts));

        // Update keyboard
        updateKeyboardConcepts(20, textView, secondTextView);

        keyboardConceptsDialog.setOnShowListener(dialog -> {
            // Change background of textView
            textView.setBackgroundResource(R.drawable.box_with_round_selected);
        });

        keyboardConceptsDialog.setOnDismissListener(dialog -> {
            // Change background of textView
            textView.setBackgroundResource(R.drawable.box_with_round);

            // Enable back
            ((ConCatActivity) activity).back = true;
        });

        // Resize buttons
        ArrayList<Button> buttons = new ArrayList<>();
        buttons.addAll(btnctNumbers);
        buttons.addAll(btnctLetters.stream().filter(b -> b != btnctSpace).collect(Collectors.toList()));
        SizeAdapter.changeHW(activity, buttons);
        ////////////////////////////////////////

        // Show dialog
        showDialog(keyboardConceptsDialog);
    }

    private void keyClickEvent(String string, int maxLen, TextView textView, TextView secondTextView) {
        String txt = textView.getText().toString();

        if (string.equals("_") && txt.length() == 0) return;


        if (len(txt) < maxLen) {
            if (txt.endsWith(" ")) {
                if (string.equals("_"))
                    return;
                else
                    string = string.toUpperCase();
            } else
                string = string.toLowerCase();
            txt += len(txt) == 0 || textView.isAllCaps() ? string.toUpperCase() : string;
            textView.setText(txt.replace("_", " "));
            if (isNotNull(secondTextView))
                secondTextView.setText(txt.replace("_", " "));
        }

        conceptsAdapter.refreshConcepts(findConceptsByName(txt, concepts));
    }

    private void updateKeyboardConcepts(int maxLen, TextView textView, TextView secondTextView) {
        for (Button b : btnctNumbers) {
            b.setEnabled(true);
            b.setBackgroundResource(R.drawable.keyboard_key);
            b.setOnClickListener(v -> keyClickEvent(b.getText().toString().toLowerCase(), maxLen, textView, secondTextView));
        }

        for (Button b : btnctLetters) {
            b.setEnabled(true);
            b.setBackgroundResource(R.drawable.keyboard_key);
            b.setOnClickListener(v -> keyClickEvent(b.getText().toString().toLowerCase(), maxLen, textView, secondTextView));
        }

        btnctSpace.setEnabled(true);
        btnctSpace.setBackgroundResource(R.drawable.keyboard_key);

        btnctEnter.setOnClickListener(v -> {
            // Get text
            String txt = textView.getText().toString().trim();

            if (txt.length() > 0 && conceptsAdapter.fragment != null && conceptsAdapter.fragment.isVisible()) {
                // Add movement
                conceptsAdapter.addMovement(txt, conceptsAdapter.concepts.stream().anyMatch(c -> c.getName().toLowerCase().equals(txt.toLowerCase())));
            } else dismissDialog(keyboardConceptsDialog);
        });

        btnctDelete.setOnClickListener(v -> {
            String txt = textView.getText().toString();

            if (len(txt) > 0) {
                textView.setText(txt.substring(0, len(txt) - 1));
                if (isNotNull(secondTextView))
                    secondTextView.setText(txt.substring(0, len(txt) - 1));
                conceptsAdapter.refreshConcepts(findConceptsByName(txt.substring(0, len(txt) - 1), concepts));
            }
        });

        btnctDelete.setOnLongClickListener(v -> {
            textView.setText("");
            if (isNotNull(secondTextView))
                secondTextView.setText("");
            conceptsAdapter.refreshConcepts(findConceptsByName("", concepts));
            return false;
        });
    }

    // SevenZip utilities
    private void compress() {
        File outFile = new File(activity.getFilesDir(), "db.7z");
        File file = new File(activity.getDatabasePath("db.sqlite3").getPath());
        try (SevenZOutputFile out = new SevenZOutputFile(outFile)) {
            SevenZArchiveEntry entry = out.createArchiveEntry(file, "db.sqlite3");
            out.putArchiveEntry(entry);

            FileInputStream in = new FileInputStream(file);
            byte[] b = new byte[1024];
            int count = 0;
            while ((count = in.read(b)) > 0) {
                out.write(b, 0, count);
            }
            out.closeArchiveEntry();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void decompress() {
        try {
            SevenZFile sevenZFile = new SevenZFile(new File(activity.getFilesDir(), "db.7z"));
            SevenZArchiveEntry entry;
            while ((entry = sevenZFile.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    continue;
                }
                File curfile = new File(activity.getDatabasePath(entry.getName()).getPath());
                File parent = curfile.getParentFile();
                if (!parent.exists()) {
                    parent.mkdirs();
                }
                FileOutputStream out = new FileOutputStream(curfile);
                byte[] content = new byte[(int) entry.getSize()];
                sevenZFile.read(content, 0, content.length);

                out.write(content);
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    ///////////////////////////////

    // Restoration utilities
    private void restoreData() {
        __wait__("backup.json");
        connect();
        FirebaseStorage.getInstance().getReference("SRV/devs/" + devId + "/db.7z").getFile(new File(activity.getFilesDir(), "db.7z"))
                .addOnSuccessListener(taskSnapshot -> {
                    decompress();
                    new DataRestoration().restoreData(this, true);
                }).addOnFailureListener(e -> new DataRestoration().restoreData(this, true));
    }

    public void startAccount() {
        if (hasSameIds()) {
            ////////////////////////////
            // Get default account id //
            ////////////////////////////
            // Is there the default account id?
            if (def.equals("") || count("accounts", "id = ?", new String[]{def}) == 0) {
                /////////////////////////////////
                // Set a correct id as default //
                /////////////////////////////////
                // Set default account
                setDefaultAccount();
            }

            /////////////////////////////////
            // Get data of default account //
            /////////////////////////////////
            // Get data by default account id
            initializeAccount();

            // Set FATAL_ERROR as false
            FATAL_ERROR = false;

            // Initialize UI
            prepareActivity(activity);
        } else {
            // Accounts as default
            setAccountsAsDefault();
        }
    }
    /////////////////////////////

    // SQLite database utilities

    public void connect() {
        if (connection == null)
            connection = new SQLiteConnection(activity, "db.sqlite3", null, 1);
    }

    private void disconnect() {
        if (connection != null) {
            connection.close();
            connection = null;
        }
    }

    public SQLiteDatabase write() {
        return connection.getWritableDatabase();
    }

    public SQLiteDatabase read() {
        return connection.getReadableDatabase();
    }

    private long count(String table) {
        return DatabaseUtils.queryNumEntries(read(), table);
    }

    public long count(String table, String selection, String[] selectionArgs) {
        return DatabaseUtils.queryNumEntries(read(), table, selection, selectionArgs);
    }

    private static final long K = 1024;
    private static final long M = K * K;
    private static final long G = M * K;
    private static final long T = G * K;

    public static String convertToStringRepresentation(final long value) {
        final long[] dividers = new long[]{T, G, M, K, 1};
        final String[] units = new String[]{"TB", "GB", "MB", "KB", "B"};
        if (value < 1)
            throw new IllegalArgumentException("Invalid file size: " + value);
        String result = null;
        for (int i = 0; i < dividers.length; i++) {
            final long divider = dividers[i];
            if (value >= divider) {
                result = format(value, divider, units[i]);
                break;
            }
        }
        return result;
    }

    private static String format(final long value,
                                 final long divider,
                                 final String unit) {
        final double result =
                divider > 1 ? (double) value / (double) divider : (double) value;
        return new DecimalFormat("#,##0.#").format(result) + " " + unit;
    }

    @SuppressLint("Recycle")
    public boolean exceedsTheMaxLimitOf(String object, boolean toast) {
        if (doesTheDatabaseExist() && !isDatabaseCorrupt()) {
            if (exceedsTheAllowedDbSize()) {
                String msg = activity.getString(R.string.You_have_reached_the_maximum_data_size);
                if (toast) toast(activity, WARNING_TOAST, msg, Toast.LENGTH_SHORT);
                return true;
            } else {
                String msg = activity.getString(R.string.You_have_reached_the_maximum_OBJECT_limit);
                boolean exceedsLimit = false;
                if (object != null) {
                    switch (object) {
                        case "accounts":
                            msg = msg.replace("OBJECT", activity.getString(R.string.Accounts));
                            exceedsLimit = count(object) >= LIMIT_ACCOUNTS;
                            break;
                        case "concepts":
                            msg = msg.replace("OBJECT", activity.getString(R.string.Concepts));
                            exceedsLimit = count(object) >= LIMIT_CONCEPTS;
                            break;
                        case "gp":
                            msg = msg.replace("OBJECT", activity.getString(R.string.Group_Payments));
                            Cursor c = read().rawQuery("select count(id)," +
                                    "(select sum(value) from movements where conceptId = m.id and type = 'TR') as p," +
                                    "(select sum(value) from movements where conceptId = m.id and type in('TR','HA')) as t " +
                                    "from movements m where type = 'GP' and (p is null or p != t)", null);
                            if (c.moveToFirst()) exceedsLimit = c.getInt(0) >= LIMIT_GP;
                            else exceedsLimit = true;
                            break;
                        case "sh":
                            msg = msg.replace("OBJECT", activity.getString(R.string.Debts));
                            exceedsLimit = count("movements", "type = ?", new String[]{"SH"}) >= LIMIT_SH;
                            break;
                        case "ha":
                            msg = msg.replace("OBJECT", activity.getString(R.string.Have_Payments));
                            exceedsLimit = count("movements", "type = ?", new String[]{"HA"}) >= LIMIT_IND_HA;
                            break;
                    }
                }
                if (exceedsLimit && toast) toast(activity, WARNING_TOAST, msg, Toast.LENGTH_SHORT);
                return exceedsLimit;
            }
        } else {
            FATAL_ERROR = true;
            activity.onBackPressed();
        }
        return true;
    }

    public boolean doesTheDatabaseExist() {
        return new File(activity.getDatabasePath("db.sqlite3").getPath()).exists();
    }

    @SuppressLint("Recycle")
    public boolean isDatabaseCorrupt() {
        connect();
        Cursor c_quick_check = read().rawQuery("pragma quick_check;", null);
        if (c_quick_check.moveToFirst()) {
            boolean ok = c_quick_check.getString(0).equals("ok");
            if (ok && !exceedsTheNotAllowedDbSize()) {
                ArrayList<String> tables = getTables();
                if (tables.size() == 3) {
                    if (checkTables(tables)) {
                        for (String table : tables) {
                            String[] columns = getColumns(table);
                            if (columns != null) {
                                if (!checkColumns(table, columns) || !checkLimit(table) || !checkTableData(table))
                                    return true;
                            } else return true;
                        }
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public boolean exceedsTheAllowedDbSize() {
        connect();
        return read().getPageSize() > (read().getMaximumSize() / 2) || read().getPageSize() > config.getMaxSize();
    }

    public boolean exceedsTheNotAllowedDbSize() {
        connect();
        return read().getPageSize() > (read().getMaximumSize() / 2) || read().getPageSize() > config.getMaxSize() * 2;
    }

    @SuppressLint("Recycle")
    private ArrayList<String> getTables() {
        Cursor c = read().rawQuery("select name from sqlite_master where type = 'table' and name not in('android_metadata','sqlite_sequence')", null);
        ArrayList<String> tables = new ArrayList<>();
        while (c.moveToNext())
            tables.add(c.getString(0));
        return tables;
    }

    @SuppressLint("Recycle")
    private String[] getColumns(String table) {
        return read().query(table, null, null, null, null, null, null).getColumnNames();
    }

    private boolean checkTables(ArrayList<String> tables) {
        return tables.contains("accounts") &&
                tables.contains("concepts") &&
                tables.contains("movements");
    }

    @SuppressLint("Recycle")
    private boolean checkColumns(String table, String[] columns) {
        switch (table) {
            case "accounts":
                if (columns.length == 3) {
                    String[] columnNames = {"id", "currency", "money"};
                    if (Arrays.equals(columns, columnNames)) {
                        for (int i = 0; i < columns.length; i++) {
                            Cursor c = read().rawQuery("select typeof (" + columns[i] + ") from " + table, null);
                            if (c.moveToFirst()) {
                                String type = c.getString(0);
                                if (i == 0 || i == 1) {
                                    if (!type.equals("text")) return false;
                                } else if (!type.equals("real")) return false;
                            }
                        }
                    } else return false;
                } else return false;
                break;
            case "concepts":
                if (columns.length == 3) {
                    String[] columnNames = {"id", "accountId", "name"};
                    if (Arrays.equals(columns, columnNames)) {
                        for (int i = 0; i < columns.length; i++) {
                            Cursor c = read().rawQuery("select typeof (" + columns[i] + ") from " + table, null);
                            if (c.moveToFirst()) {
                                String type = c.getString(0);
                                if (i == 0) {
                                    if (!type.equals("integer")) return false;
                                } else if (!type.equals("text")) return false;
                            }
                        }
                    } else return false;
                } else return false;
                break;
            case "movements":
                if (columns.length == 8) {
                    String[] columnNames = {"id", "accountId", "conceptId", "date", "location", "subCategoryId", "type", "value"};
                    if (Arrays.equals(columns, columnNames)) {
                        for (int i = 0; i < columns.length; i++) {
                            Cursor c = read().rawQuery("select typeof (" + columns[i] + ") from " + table, null);
                            if (c.moveToFirst()) {
                                String type = c.getString(0);
                                if ((i == 2 || i == 4 || i == 5) && type.equals("null")) { /* Nullable columns */ } else {
                                    if (i == 1 || i == 3 || i == 4 || i == 5 || i == 6) {
                                        if (!type.equals("text")) return false;
                                    } else if (i == 0 || i == 2) {
                                        if (!type.equals("integer")) return false;
                                    } else if (!type.equals("real")) return false;
                                }
                            }
                        }
                    } else return false;
                } else return false;
                break;
        }
        return true;
    }

    @SuppressLint("Recycle")
    private boolean checkLimit(String table) {
        switch (table) {
            case "accounts":
                return count(table) <= LIMIT_ACCOUNTS;
            case "concepts":
                return count(table) <= LIMIT_CONCEPTS;
            case "movements":
                boolean sh_ha = count(table, "type = 'SH'", null) <= LIMIT_SH &&
                        count(table, "type = 'HA' and conceptId is null", null) <= LIMIT_IND_HA &&
                        count(table, "type = 'HA' and conceptId is not null", null) <= LIMIT_GP_HA;
                Cursor c = read().rawQuery("select count(id)," +
                        "(select sum(value) from movements where conceptId = m.id and type = 'TR') as p," +
                        "(select sum(value) from movements where conceptId = m.id and type in('TR','HA')) as t " +
                        "from movements m where type = 'GP' and (p is null or p != t)", null);
                if (c.moveToFirst()) return sh_ha && c.getInt(0) <= LIMIT_GP;
                return false;
            default:
                return false;
        }
    }

    private boolean checkTableData(String table) {
        switch (table) {
            case "accounts":
                return checkAccountsTable();
            case "concepts":
                return checkConceptsTable();
            case "movements":
                return checkMovementsTable();
            default:
                return false;
        }
    }

    @SuppressLint("Recycle")
    private boolean checkAccountsTable() {
        Cursor c = read().rawQuery("select id, currency, money from accounts order by rowid asc", null);
        int i = 0;
        if (c.getCount() > 0) {
            if (c.getCount() == device.accounts.size()) {
                while (c.moveToNext()) {
                    if (!c.getString(0).equals(device.accounts.get(i).getId()) ||
                            ((c.getString(1).length() != 3 ||
                            !c.getString(1).chars().allMatch(Character::isLetter) ||
                            !c.getString(1).chars().allMatch(Character::isUpperCase)) &&
                            !TEST_CREDENTIALS_PHONE_NUMBER.equals(device.getPhone())) ||
                            !doesThisCurrencyExist(c.getString(1)) ||
                            c.getDouble(2) > MAX_MONEY) return false;
                    i++;
                }
            } else return false;
        }
        return true;
    }

    @SuppressLint("Recycle")
    private boolean checkConceptsTable() {
        return count("concepts", "accountId is null or " +
                "accountId not in(select id from accounts) or " +
                "name is null or " +
                "length(name) = 0 or " +
                "length(name) > 20 or " +
                "(length(name) - length(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(name " +
                ",'Q','') ,'W','') ,'E','') ,'R','') ,'T','') ,'Y','') ,'U','') ,'I','') ,'O','') ,'P','') " +
                " ,'A','') ,'S','') ,'D','') ,'F','') ,'G','') ,'H','') ,'J','') ,'K','') ,'L','') ,'Ñ','') " +
                " ,'Z','') ,'X','') ,'C','') ,'V','') ,'B','') ,'N','') , 'M', '' ) ) ) " +
                "> length(trim(name)) - length(replace(trim(name), ' ', '')) + 1 or " +
                "length(trim(name)) - length(replace(trim(name), ' ', '')) + 1 != length(name) - length(replace(name, ' ', '')) + 1", null) == 0
                &&
                count("concepts", "length ( replace ( replace( replace( replace( replace( replace( replace( replace( replace( replace( replace( replace( replace( replace( replace( replace( replace( replace( replace( replace( replace( replace( replace( replace( replace( replace( replace( replace(upper(name)" +
                        ",'Q','') ,'W','') ,'E','') ,'R','') ,'T','') ,'Y','') ,'U','') ,'I','') ,'O','') ,'P','')" +
                        " ,'A','') ,'S','') ,'D','') ,'F','') ,'G','') ,'H','') ,'J','') ,'K','') ,'L','') ,'Ñ','')" +
                        " ,'Z','') ,'X','') ,'C','') ,'V','') ,'B','') ,'N','') , 'M', '' ) , ' ', '' ) ) > 0 " +
                        "and " +
                        "replace ( replace( replace( replace( replace( replace( replace( replace( replace( replace( replace( replace( replace( replace( replace( replace( replace( replace( replace( replace( replace( replace( replace( replace( replace( replace( replace( replace(upper(name)" +
                        ",'Q','') ,'W','') ,'E','') ,'R','') ,'T','') ,'Y','') ,'U','') ,'I','') ,'O','') ,'P','')" +
                        " ,'A','') ,'S','') ,'D','') ,'F','') ,'G','') ,'H','') ,'J','') ,'K','') ,'L','') ,'Ñ','')" +
                        " ,'Z','') ,'X','') ,'C','') ,'V','') ,'B','') ,'N','') , 'M', '' ) , ' ', '' ) not regexp '^[[:digit:]]+$'", null) == 0;
    }

    @SuppressLint("Recycle")
    private boolean checkMovementsTable() {
        // Universal
        if (count("movements", "accountId is null or " +
                "accountId not in(select id from accounts) or " +
                "type is null or " +
                "type not in('EX','IN','I','E','TR','SH','HA','GP') or " +
                "value is null or " +
                "abs(value) > 9999999.99 or " +
                "abs(value) < 0.01 or " +
                "date is null or " +
                "strftime('%Y-%m-%d %H:%M:%S', date) is null or " +
                //"datetime(date) > datetime('now','localtime') or " +
                //"datetime(date) < datetime('2021-09-14 00:00:00') or " +
                "(type in('E','I') and (conceptId is null or conceptId not in(select id from concepts))) or " +
                "(type in('EX','IN') and (subCategoryId is null or subCategoryId not in(" +
                "'0'," +
                "'1#0','1#1','1#2','1#3'," +
                "'2#0','2#1','2#2','2#3','2#4','2#5','2#6','2#7'," +
                "'3#0','3#1','3#2','3#3','3#4','3#5','3#6'," +
                "'4#0','4#1','4#2','4#3','4#4','4#5','4#6','4#7','4#8','4#9','4#10','4#11','4#12','4#13'," +
                "'5#0','5#1','5#2','5#3','5#4','5#5','5#6','5#7','5#8'," +
                "'6#0','6#1','6#2','6#3','6#4','6#5','6#6','6#7','6#8'," +
                "'7#0','7#1','7#2','7#3','7#4','7#5','7#6','7#7'," +
                "'8#0','8#1','8#2','8#3','8#4'," +
                "'9#0','9#1','9#2','9#3'))) or " +
                "(type in('I','EX','SH') and value > 0) or " +
                "(type in('E','IN','HA','GP') and value < 0)", null) > 0) return false;
        // Group names [ Bad structure ]
        if (count("movements", "type = 'GP' and " +
                "(" +
                "location is null or " +
                "length(location) = 0 or " +
                "length(location) > 20 or " +
                "length(trim(location)) - length(replace(trim(location), ' ', '')) + 1 != length(location) - length(replace(location, ' ', '')) + 1" +
                ")", null) > 0
                ||
                count("movements", "type = 'GP' and " +
                        "(length(location) - length(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(location " +
                        ",'Q','') ,'W','') ,'E','') ,'R','') ,'T','') ,'Y','') ,'U','') ,'I','') ,'O','') ,'P','') " +
                        " ,'A','') ,'S','') ,'D','') ,'F','') ,'G','') ,'H','') ,'J','') ,'K','') ,'L','') ,'Ñ','') " +
                        " ,'Z','') ,'X','') ,'C','') ,'V','') ,'B','') ,'N','') , 'M', '' ) ) ) " +
                        "> length(trim(location)) - length(replace(trim(location), ' ', '')) + 1", null) > 0)
            return false;
        // Group names [ Bad data ]
        Cursor c = read().rawQuery("select location from movements where type = 'GP'", null);
        while (c.moveToNext()) {
            String location = c.getString(0).replaceAll("[\\d]", "").replaceAll(" ", "");
            if (location.chars().count() > 0 && location.chars().anyMatch(ch -> !Character.isLetter(ch)))
                return false;
        }
        // LookUpKeys
        if (count("movements", "type in ('SH','HA','TR') and " +
                "(location is null or " +
                "length(location) = 0 or " +
                "length(location) > 555 or " +
                "replace( replace( replace( replace( replace( replace( replace( replace( replace( replace( replace( replace( replace( replace( replace( replace( replace( replace( replace( replace( replace( replace( replace( replace( replace( replace( replace(location" +
                ",'q','') ,'w','') ,'e','') ,'r','') ,'t','') ,'y','') ,'u','') ,'i','') ,'o','') ,'p','')" +
                " ,'a','') ,'s','') ,'d','') ,'f','') ,'g','') ,'h','') ,'j','') ,'k','') ,'l','') ,'ñ','')" +
                " ,'z','') ,'x','') ,'c','') ,'v','') ,'b','') ,'n','') , 'm', '' ) not regexp '^[[:digit:]]+$')", null) > 0)
            return false;
        // Locations [ Bad structure ]
        if (count("movements", "type in('EX','IN','I','E') and " +
                "location not null and " +
                "(" +
                "length(location) = 0 or " +
                "length(location) > 555 or " +
                "length(location) - length(replace(location, '·', '')) != 4" +
                ")", null) > 0) return false;
        // Locations [ Bad data ]
        c = read().rawQuery("select location " +
                "from movements " +
                "where type in('EX','IN','I','E') and " +
                "location not null and " +
                "length(location) < 555", null);
        while (c.moveToNext()) {
            String location = c.getString(0).replaceAll("[\\d]", "").replaceAll("·", "").replaceAll(" ", "");
            if (location.chars().count() > 0 && location.chars().anyMatch(ch -> !Character.isLetter(ch)))
                return false;
        }
        return true;
    }

    @SuppressLint("Recycle")
    private boolean hasSameIds() {
        connect();

        Cursor c = read().rawQuery("select id from accounts", null);

        if (c.getCount() == device.getAccounts().size()) {
            while (c.moveToNext()) {
                if (device.getAccounts().stream().noneMatch(a -> a.getId().equals(c.getString(0))))
                    return false;
            }
        } else return false;
        return true;
    }

    @SuppressLint("Recycle")
    public int getMaxId(String table) {
        Cursor c = read().rawQuery("select max(id) from " + table, null);
        return c.moveToFirst() ? c.getInt(0) : 0;
    }

    public void deleteLocalFiles() {
        File filesDir = new File(activity.getFilesDir().getAbsolutePath());
        File databasesDir = new File(activity.getDataDir() + "/databases");
        try {
            if (filesDir.exists()) FileUtils.deleteDirectory(filesDir);
            if (databasesDir.exists()) {
                File[] listFiles = databasesDir.listFiles();
                if (null != listFiles)
                    for (File file : listFiles)
                        if (file.getName().startsWith("db"))
                            deleteFile(file);
            }
        } catch (IOException ignored) {
        }
    }

    public void deleteCloudData(String devId, Callable<Void> func) {
        // Delete db.7z from storage
        FirebaseStorage.getInstance().getReference("SRV/devs/" + devId + "/db.7z").delete()
                .addOnCompleteListener(aVoid -> {
                    // Delete FirebaseDatabase data
                    FirebaseDatabase.getInstance().getReference("devices/" + devId).removeValue()
                            .addOnCompleteListener(aVoid1 -> {
                                // Delete Firebase store data
                                FirebaseFirestore.getInstance().collection("devices").document(devId).delete()
                                        .addOnCompleteListener(aVoid2 -> {
                                            try {
                                                func.call();
                                            } catch (Exception ignored) {
                                                __wait__("outside_the_system.json");
                                            }
                                        });
                            });
                });
    }

    private void deleteData() {
        // b785f82f0a2dea8b
        deleteLocalFiles();
        FirebaseStorage.getInstance().getReference("SRV/devs/" + devId + "/db.7z").delete().addOnCompleteListener(task -> FirebaseDatabase.getInstance().getReference("devices/" + devId).removeValue().addOnSuccessListener(aVoid -> {
            FirebaseFirestore.getInstance().collection("devices").document(devId).delete().addOnSuccessListener(aVoid1 -> {
                toast(activity, WARNING_TOAST, activity.getString(R.string.For_some_reason_the_data_has_been_deleted), Toast.LENGTH_SHORT);
            });
        }));
    }

    private void refreshData() {
        deleteLocalFiles();
        FirebaseDatabase.getInstance().getReference("devices/" + devId + "/autR").removeValue().addOnSuccessListener(aVoid -> startAccessing());
    }


    // try -> insert movement [ CONCEPT | category ]
    public void insertMovement(Concept concept, String name, String txt) {
        if (newValue != 0) {
            if (!exceedsTheMaxLimitOf(null, true) && (isNotNull(concept) || !exceedsTheMaxLimitOf("concepts", true))) {
                // Try to connect to the internet
                tryToConnectToTheInternet(() -> {
                    if (doesTheDatabaseExist() && !isDatabaseCorrupt())
                        letInsertMovement(concept, name, txt);
                    else {
                        System.FATAL_ERROR = true;
                        activity.onBackPressed();
                    }
                    return null;
                });
            } else {
                // Enable back
                ((ConCatActivity) activity).back = true;

                // Back
                activity.onBackPressed();
            }
        } else activity.onBackPressed();
    }

    // let -> insert movement [ CONCEPT | category ]
    @SuppressLint("Recycle")
    private void letInsertMovement(final Concept concept, String name, String txt) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;
        LocationServices.getFusedLocationProviderClient(activity).getLastLocation().addOnCompleteListener(task -> {
            // Initialize location string builder
            StringBuilder locationStringBuilder = new StringBuilder();

            // Initialize location
            android.location.Location location = task.getResult();
            if (location != null) {
                try {
                    // Initialize geoCoder
                    Geocoder geocoder = new Geocoder(activity, Locale.getDefault());

                    // Initialize address list
                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                    if (addresses != null && addresses.size() > 0) {
                        // Get first address
                        Address a = addresses.get(0);

                        String[] strings = {a.getThoroughfare(), a.getLocality(), a.getSubAdminArea(), a.getAdminArea(), a.getCountryName()};

                        if (Arrays.stream(strings).noneMatch(Objects::isNull))
                            locationStringBuilder
                                    .append(strings[0]).append("·")
                                    .append(strings[1]).append("·")
                                    .append(strings[2]).append("·")
                                    .append(strings[3]).append("·")
                                    .append(strings[4]);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // Initialize location string
            String locationStr = locationStringBuilder.length() > 0 ? locationStringBuilder.toString() : null;

            // Initialize value
            double value = calcSymbol == '+' ? newValue : -newValue;

            // Initialize type
            String type = value > 0 ? "E" : "I";

            // Initialize now date
            String now = getLocalDateTimeNow();

            // Send request
            newRequest(config.isBm(), accountPos(), "I " + type + " " + now.replace(" ", "_") + " " + (locationStr != null ? locationStr.replace(" ", "_") + "," : "") + (concept != null ? concept.getId() : ":" + name.replace(" ", "_")) + "," + value, () -> {
                // Connect to SQLite
                connect();

                // Declare conceptId
                int conceptId;

                // Is concept null?
                if (isNull(concept)) {
                    ////////////////////
                    // Insert concept //
                    ////////////////////
                    // Execute request in SQLite
                    write().execSQL("insert into concepts (accountId, name) values(?,?)", new String[]{def, name});

                    // Get last concept
                    Cursor c = read().rawQuery("select max(id), name from concepts where accountId = ? ", new String[]{def});
                    c.moveToFirst();

                    // Set conceptId
                    conceptId = c.getInt(0);
                } else conceptId = concept.getId();


                // Execute request in SQLite
                write().execSQL("insert into movements (accountId, conceptId, date, location, type, value) values(?,?,?,?,?,?)",
                        new String[]{def, String.valueOf(conceptId), now, locationStr, type, String.valueOf(value)});

                // Set money
                account.setMoney(newResult);

                // Change money in SQLite
                changeMoney();
                //////////////

                // Month benefits or expenses?
                if (value > 0) account.setMonthBenefits(account.getMonthBenefits() + value);
                else account.setMonthExpenses(account.getMonthExpenses() + value);
                //////////////////////////////////////////////////////////////////

                // Reset newValue, newResult and calcSymbol
                newValue = 0.00;
                newResult = 0.00;
                calcSymbol = '?';
                /////////////////

                ///////////////
                // UI DESIGN //
                ///////////////
                // Refresh list
                conceptsAdapter.concepts = findConceptsByName(txt.toLowerCase(), getConcepts());

                // Notify
                conceptsAdapter.notifyItemRangeChanged(0, conceptsAdapter.concepts.size());

                // Set con
                ConCatActivity.setCon(false);

                // Back
                new Handler().postDelayed(() -> {
                    // Enable back
                    ((ConCatActivity) activity).back = true;

                    // Go back
                    activity.onBackPressed();
                }, 500);
                return null;
            });

        });
    }

    // try -> insert movement [ concept | CATEGORY ]
    public void insertMovement(CategoryFragment fragment, String subCategoryId) {

        if (newValue != 0) {
            // Disable back
            ((ConCatActivity) activity).back = false;

            // Disable btnGoToConCat
            ((ConCatActivity) activity).btnGoToConCat.setEnabled(false);

            // Initialize RecyclerViewDisabler
            RecyclerView.OnItemTouchListener disabler = new RecyclerViewDisabler();

            // Disable recyclerView
            disableUI(disabler, fragment.rvCategories, null, null, null);

            if (!exceedsTheMaxLimitOf(null, true)) {
                // Try to connect to the internet
                tryToConnectToTheInternet(() -> {
                    if (doesTheDatabaseExist() && !isDatabaseCorrupt())
                        letInsertMovement(fragment, subCategoryId);
                    else {
                        System.FATAL_ERROR = true;
                        activity.onBackPressed();
                    }
                    return null;
                });
            } else {
                // Enable back
                ((ConCatActivity) activity).back = true;

                // Back
                activity.onBackPressed();
            }
        } else activity.onBackPressed();
    }

    // let -> insert movement [ concept | CATEGORY ]
    private void letInsertMovement(CategoryFragment fragment, String subCategoryId) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;
        LocationServices.getFusedLocationProviderClient(activity).getLastLocation().addOnCompleteListener(task -> {
            // Initialize location string builder
            StringBuilder locationStringBuilder = new StringBuilder();

            // Initialize location
            android.location.Location location = task.getResult();
            if (location != null) {
                try {
                    // Initialize geoCoder
                    Geocoder geocoder = new Geocoder(activity, Locale.getDefault());

                    // Initialize address list
                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                    if (addresses != null && addresses.size() > 0) {
                        // Get first address
                        Address a = addresses.get(0);

                        String[] strings = {a.getThoroughfare(), a.getLocality(), a.getSubAdminArea(), a.getAdminArea(), a.getCountryName()};

                        if (Arrays.stream(strings).noneMatch(Objects::isNull))
                            locationStringBuilder
                                    .append(strings[0]).append("·")
                                    .append(strings[1]).append("·")
                                    .append(strings[2]).append("·")
                                    .append(strings[3]).append("·")
                                    .append(strings[4]);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // Initialize location string
            String locationStr = locationStringBuilder.length() > 0 ? locationStringBuilder.toString() : null;

            // Initialize value
            double value = calcSymbol == '+' ? newValue : -newValue;

            // Initialize type
            String type = value > 0 ? "IN" : "EX";

            // Initialize now date
            String now = getLocalDateTimeNow();

            // Send request
            newRequest(config.isBm(), accountPos(), "I " + type + " " + now.replace(" ", "_") + " " + (locationStr != null ? locationStr.replace(" ", "_") + "," : "") + subCategoryId + "," + value, () -> {
                // Connect to SQLite
                connect();

                // Execute request in SQLite
                write().execSQL("insert into movements (accountId, subCategoryId, date, location, type, value) values(?,?,?,?,?,?)",
                        new String[]{def, subCategoryId, now, locationStr, type, String.valueOf(value)});

                // Set money
                account.setMoney(newResult);

                // Change money in SQLite
                changeMoney();
                //////////////

                // Month benefits or expenses?
                if (value > 0) account.setMonthBenefits(account.getMonthBenefits() + value);
                else account.setMonthExpenses(account.getMonthExpenses() + value);
                //////////////////////////////////////////////////////////////////

                // Reset newValue, newResult and calcSymbol
                newValue = 0.00;
                newResult = 0.00;
                calcSymbol = '?';
                /////////////////

                ///////////////
                // UI DESIGN //
                ///////////////
                // Refresh list
                fragment.categoryAdapter.categories = getCategories();

                // Notify
                fragment.categoryAdapter.notifyItemChanged(Integer.parseInt(subCategoryId.split("#")[0]));

                // Set cat
                ConCatActivity.setCat(false);

                // Back
                new Handler().postDelayed(() -> {
                    // Enable back
                    ((ConCatActivity) activity).back = true;

                    // Go back
                    activity.onBackPressed();
                }, 500);
                return null;
            });
        });
    }

    @SuppressLint("Recycle")
    private void initializeAccount() {
        // Get currency and money
        Cursor c = read().rawQuery("select currency, money from accounts where id = ?", new String[]{def});
        c.moveToFirst();
        account = new Account();
        account.setId(def);
        account.setCurrency(c.getString(0));
        account.setMoney(c.getDouble(1));

        /////////////////////
        //      TYPES      //
        /////////////////////
        // EX: Expense [-] //
        // IN: Income  [+] //
        // I:  Invert  [-] //
        // E:  Evert   [+] //
        // SH: Should  [-] //
        // HA: Have    [+] //
        /////////////////////
        // Get the sum of expenses for this month
        sumMonthExpenses();

        // Get the sum of benefits for this month
        sumMonthBenefits();

        // Get money to owe [Should(-)]
        sumShould();

        // Get money of transact
        sumTransactions();

        // Get money to receive [Have(+)]
        sumHave();
    }

    private void sumMonthExpenses() {
        // Initialize current month
        @SuppressLint("SimpleDateFormat") String month = new SimpleDateFormat("MM").format(new Date());

        // Get the sum of expenses for this month
        @SuppressLint("Recycle") Cursor c = read().rawQuery("select sum(value) from movements where accountId = ? and " +
                "value < 0 and type in('EX','I','GP','TR') and " +
                "date like '%-" + month + "-%'", new String[]{def});
        account.setMonthExpenses(c.moveToFirst() ? c.getDouble(0) : 0);
    }

    private void sumMonthBenefits() {
        // Initialize current month
        @SuppressLint("SimpleDateFormat") String month = new SimpleDateFormat("MM").format(new Date());

        // Get the sum of benefits for this month
        @SuppressLint("Recycle") Cursor c = read().rawQuery("select sum(value) from movements where accountId = ? and " +
                "value > 0 and type in('IN','E','TR') and " +
                "date like '%-" + month + "-%'", new String[]{def});
        account.setMonthBenefits(c.moveToFirst() ? c.getDouble(0) : 0);
    }

    private void sumShould() {
        // Get money to owe [Should(-)]
        @SuppressLint("Recycle") Cursor c = read().rawQuery("select sum(value) from movements where accountId = ? and " +
                "type = 'SH'", new String[]{def});
        account.setShould(c.moveToFirst() ? c.getDouble(0) : 0);
    }

    private void sumTransactions() {
        // Get money of transact
        @SuppressLint("Recycle") Cursor c = read().rawQuery("select sum(value) from movements where accountId = ? and " +
                "type = 'TR'", new String[]{def});
        account.setTransact(c.moveToFirst() ? c.getDouble(0) : 0);
    }

    private void sumHave() {
        // Get money to receive [Have(+)]
        @SuppressLint("Recycle") Cursor c = read().rawQuery("select sum(value) from movements where accountId = ? and " +
                "type = 'HA'", new String[]{def});
        account.setHave(c.moveToFirst() ? c.getDouble(0) : 0);
    }


    ////////////
    // FRIEND //
    ////////////
    @SuppressLint("Recycle")
    public ArrayList<Friend> getFriends(String type) {
        // Connect to SQLite
        connect();

        // type = 'SH' or type = 'HA'
        Cursor c = read().rawQuery("select location " +
                "from movements m " +
                "where accountId = ? and type = '" + type + "' " +
                "group by location " +
                "order by abs(sum(value)) desc, min(date) asc", new String[]{def});
        ArrayList<Friend> friends = new ArrayList<>();
        while (c.moveToNext()) {
            // Initialize friend
            Friend friend = new Friend();

            // Set lookUpKey
            friend.setLookUpKey(c.getString(0));

            // Set payments
            friend.payments = getPayments(friend.getLookUpKey(), type);

            // Add friend
            friends.add(friend);
        }
        return friends;
    }

    private ArrayList<Payment> getPayments(String lookUpKey, String type) {
        @SuppressLint("Recycle") Cursor c = read().rawQuery("select id, conceptId, date, value, type = 'TR' as 'isTransaction' " +
                "from movements " +
                "where accountId = ? and (type = ? or (type = ? and conceptId not null)) and location = ? " +
                "order by abs(value) desc, date asc", new String[]{def, type, type, lookUpKey});
        ArrayList<Payment> payments = new ArrayList<>();
        while (c.moveToNext()) {
            // Initialize payment
            Payment payment = new Payment();

            // Set id
            payment.setId(c.getInt(0));

            // Set groupId
            payment.setGroupId(c.isNull(1) ? -1 : c.getInt(1));

            // Set date
            payment.setDate(c.getString(2));

            // Set value
            payment.setValue(c.getDouble(3));

            // Set transaction
            payment.setTransaction(c.getInt(4) == 1);

            // Add payment
            payments.add(payment);
        }

        if (payments.size() > 0) payments.add(0, null);

        // Return list
        return payments;
    }

    public void showRecyclerViewDialog(FriendsAdapter friendsAdapter, int pos) {
        // Initialize RecyclerViewDisabler
        if (friendsAdapter.disabler == null) friendsAdapter.disabler = new RecyclerViewDisabler();

        // Disable UI
        disableUI(friendsAdapter.disabler, friendsAdapter.rvFriends, friendsAdapter.bottomNavView, friendsAdapter.viewPager, friendsAdapter.tabLayout);

        // Initialize dialog
        recyclerDialog = buildDialog(recyclerDialog, recyclerView, 0, R.style.AppTheme_PopUp, true);

        // Set on dismiss
        recyclerDialog.setOnDismissListener(dialog -> {
            enableUI(friendsAdapter.disabler, friendsAdapter.rvFriends, friendsAdapter.bottomNavView, friendsAdapter.viewPager, friendsAdapter.tabLayout);
        });

        // Initialize recyclerView
        RecyclerView rv = recyclerView.findViewById(R.id.recyclerView);

        // Initialize adapter
        PaymentsAdapter paymentsAdapter = new PaymentsAdapter(friendsAdapter, rv, pos);

        // Set adapter
        setAdapter(rv, paymentsAdapter, false, false, new LinearLayoutManager(recyclerView.getContext(), RecyclerView.VERTICAL, false));

        // Animate view
        recyclerView.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.fade_in));

        // Show dialog
        showDialog(recyclerDialog);
    }

    public void showRecyclerViewDialog(boolean movements) {
        // Initialize dialog
        recyclerDialog = buildDialog(recyclerDialog, recyclerView, 0, R.style.AppTheme_PopUp, true);

        // Initialize recyclerView
        RecyclerView rv = recyclerView.findViewById(R.id.recyclerView);

        // Initialize adapter
        VoiceAssistantSentencesAdapter adapter = new VoiceAssistantSentencesAdapter(activity, movements);

        // Set adapter
        setAdapter(rv, adapter, false, false, new LinearLayoutManager(recyclerView.getContext(), RecyclerView.VERTICAL, false));

        // Animate view
        recyclerView.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.fade_in));

        // Show dialog
        showDialog(recyclerDialog);
    }
    //////////////////////////////////////////////////////////////////////////////////

    ///////////
    // GROUP //
    ///////////
    @SuppressLint("Recycle")
    public ArrayList<Group> getGroups(boolean paid) {
        // Connect to SQLite
        connect();

        Cursor c = read().rawQuery("select id, location, date, value," +
                "(select sum(value) from movements where conceptId = m.id and type = 'TR') as p," +
                "(select sum(value) from movements where conceptId = m.id and type in('TR','HA')) as t " +
                "from movements m where accountId = ? and type = 'GP' and " + (paid ? "p = t" : "(p is null or p != t)") + " " +
                "order by value * (select count(id) + 1 from movements where conceptId = m.id and type = 'HA') - t desc, date desc", new String[]{def});
        ArrayList<Group> groups = new ArrayList<>();
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

            // Add group?
            if (group.friends.size() >= System.LIMIT_MIN_GP_PARTICIPANTS) groups.add(group);
        }
        return groups;
    }


    private ArrayList<Friend> getFriends(int groupId) {
        @SuppressLint("Recycle") Cursor c = read().rawQuery("select location, id, min(date), sum(value), type = 'TR' " +
                "from movements m where accountId = ? and " +
                "type in('SH','HA','TR') and conceptId = ? " +
                "group by location order by date desc, value desc", new String[]{def, String.valueOf(groupId)});
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

    public ArrayList<Category> getCategories() {
        ArrayList<Category> categories = new ArrayList<>();
        String[] cats = activity.getResources().getStringArray(R.array.categories);

        for (int i = 0; i < cats.length; i++) {
            Category c = new Category(cats[i].split("\\|")[0]);
            c.subCategories = new ArrayList<>();
            if (cats[i].contains("|")) {
                String[] subCats = cats[i].split("\\|")[1].split(",");
                for (int j = 0; j < subCats.length; j++) {
                    double[] plm = getPLM(i, j);
                    c.subCategories.add(new SubCategory(subCats[j], plm[0], plm[1], plm[2]));
                }
                c.setProf(c.subCategories.stream().mapToDouble(SubCategory::getProf).sum());
                c.setLoss(c.subCategories.stream().mapToDouble(SubCategory::getLoss).sum());
                c.setMoney(c.subCategories.stream().mapToDouble(SubCategory::getMoney).sum());
            } else {
                double[] plm = getPLM(i, -1);
                c.setProf(plm[0]);
                c.setLoss(plm[1]);
                c.setMoney(plm[2]);
            }
            categories.add(c);
        }
        return categories;
    }

    @SuppressLint("Recycle")
    private double[] getPLM(int catIdx, int subCatIdx) {
        // Initialize subCategoryId
        String subCategoryId = catIdx + (subCatIdx != -1 ? "#" + subCatIdx : "");

        // Initialize [ Profits | Losses | Money ] array
        double[] plm = {0, 0, 0};

        // Get PLM
        Cursor c = read().rawQuery("select " +
                "(select sum(value) from movements where subCategoryId = move.subCategoryId and type = 'IN' and accountId = move.accountId) as p, " +
                "(select sum(value) from movements where subCategoryId = move.subCategoryId and type = 'EX' and accountId = move.accountId) as l, " +
                "(select sum(value) from movements where subCategoryId = move.subCategoryId and type in('IN','EX') and accountId = move.accountId) as m " +
                "from movements move " +
                "where accountId = ? and subCategoryId = ?", new String[]{def, subCategoryId});

        // Set PLM
        if (c.moveToFirst()) {
            plm[0] = c.isNull(0) ? 0 : c.getDouble(0);
            plm[1] = c.isNull(1) ? 0 : c.getDouble(1);
            plm[2] = c.isNull(2) ? 0 : c.getDouble(2);
        }

        // Return PLM
        return plm;
    }


    @SuppressLint("Recycle")
    public ArrayList<Concept> getConcepts() {
        // Connect to SQLite
        connect();

        // Get concepts
        ArrayList<Concept> concepts = new ArrayList<>();
        Cursor c = read().rawQuery("select id, name, " +
                "(select sum(value) from movements where conceptId = c.id and type = 'E') as p, " +
                "(select sum(value) from movements where conceptId = c.id and type = 'I') as l, " +
                "(select sum(value) from movements where conceptId = c.id and type in('E','I')) as m " +
                "from concepts c " +
                "where accountId = ? " +
                "order by abs(m) desc, name asc", new String[]{def});
        while (c.moveToNext())
            concepts.add(new Concept(
                    c.getInt(0),
                    c.getString(1),
                    c.isNull(2) ? 0 : c.getDouble(2),
                    c.isNull(3) ? 0 : c.getDouble(3),
                    c.isNull(4) ? 0 : c.getDouble(4)
            ));
        return concepts;
    }

    ///////////////////////////////////////////////////////////////////////////////

    // Firebase databases utilities
    public boolean isDocReady(DocumentSnapshot document) {
        if (document != null) {
            return document.exists();
        }
        return false;
    }

    public boolean isDataReady(DataSnapshot data) {
        if (data != null) {
            return data.exists();
        }
        return false;
    }

    public boolean isDataReady(DocumentSnapshot data) {
        if (data != null) {
            return data.exists();
        }
        return false;
    }

    public boolean isNotNull(Object o) {
        return o != null;
    }

    public boolean isNotNull(int i) {
        return i != -1;
    }

    public boolean isNull(Object o) {
        return o == null;
    }

    public void tryToConnectToTheInternet(Callable<Void> func) {
        if (!isNetworkAvailable()) {
            if (!waiting) {
                // Wait
                __wait__("connectivity.json");

                // Waiting for internet connection
                waitForInternetConnection(func);
            }
        } else {
            try {
                func.call();
            } catch (Exception ignored) {
                __wait__("outside_the_system.json");
            }
        }
    }

    private void waitForInternetConnection(Callable<Void> func) {

        Handler handler = new Handler();

        final Runnable r = new Runnable() {
            public void run() {

                if (isNetworkAvailable())
                    __not_wait__();

                if (lottieDialog == null) {
                    waiting = false;
                    handler.removeCallbacks(this);
                } else if (!lottieDialog.isShowing()) {
                    waiting = false;
                    handler.removeCallbacks(this);
                    try {
                        func.call();
                    } catch (Exception ignored) {
                        __wait__("outside_the_system.json");
                    }
                } else {
                    handler.postDelayed(this, 1000);
                }
            }
        };

        handler.postDelayed(r, 1000);
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void set(String path, Object o) {
        FirebaseDatabase.getInstance().getReference(path).setValue(o);
    }

    private int accountIndex() {
        int index = -1;
        for (int i = 0; i < device.getAccounts().size(); i++)
            if (account.getId().equals(device.getAccounts().get(i).getId()))
                return i;
        return index;
    }

    public int accountPos() {
        int index = -1;
        for (int i = 0; i < device.getAccounts().size(); i++)
            if (account.getId().equals(device.getAccounts().get(i).getId()))
                return device.getAccounts().get(i).getPos();
        return index;
    }

    private int requestsCount() {
        int count = 0;
        for (int i = 0; i < device.getAccounts().size(); i++) {
            count += device.getAccounts().get(i).getR() != null ?
                    device.getAccounts().get(i).getR().size() : 0;
            count += device.getAccounts().get(i).getAuxR() != null ?
                    device.getAccounts().get(i).getAuxR().size() : 0;
        }
        return count;
    }

    private void transferRequestsToQueue(DeviceAccount da) {
        if (isNotNull(da.getAuxR()) && da.getAuxR().size() > 0) {
            if (isNull(da.getR()))
                da.r = new ArrayList<>();
            da.getR().addAll(da.getAuxR());
        }
    }

    public void newRequest(boolean aux, int accountIndex, String request, Callable<Void> func) {

        // Get account of device
        DeviceAccount da = device.getAccounts().stream().filter(a -> a.getPos() == accountIndex).findFirst().get();

        if (!aux) {
            // Transfer requests to queue if necessary
            transferRequestsToQueue(da);

            if (isNotNull(da.getAuxR()) && da.getAuxR().size() > 0) {
                // Apply in firebase
                FirebaseDatabase.getInstance().getReference("devices/" + devId + "/accounts/" + da.getPos() + "/r").setValue(da.getR()).addOnSuccessListener(aVoid -> {
                    // Delete auxiliary requests
                    FirebaseDatabase.getInstance().getReference("devices/" + devId + "/accounts/" + da.getPos() + "/auxR").removeValue().addOnSuccessListener(aVoid1 -> {
                        // Clear auxR list
                        da.getAuxR().clear();

                        // Do request
                        doRequest(da, false, request, func);
                    });
                });
            } else doRequest(da, false, request, func);
        } else doRequest(da, true, request, func);
    }

    private void doRequest(DeviceAccount da, boolean aux, String request, Callable<Void> func) {
        // Get index of request
        int requestIndex = !aux ? (da.r != null ? da.r.size() : 0) : (da.auxR != null ? da.auxR.size() : 0);

        // Upload request
        FirebaseDatabase.getInstance().getReference("devices/" + devId + "/accounts/" + da.getPos() + "/" + (aux ? "auxR" : "r") + "/" + requestIndex).setValue(request).addOnSuccessListener(aVoid -> {
            // Add request
            if (!aux) {
                if (da.r == null)
                    da.r = new ArrayList<>();
                da.getR().add(request);
            } else {
                if (da.auxR == null)
                    da.auxR = new ArrayList<>();
                da.getAuxR().add(request);
            }

            try {
                func.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void setAccountsAsDefault() {
        tryToConnectToTheInternet(() -> {
            letSetAccountsAsDefault();
            return null;
        });
    }

    private void letSetAccountsAsDefault() {
        for (int i = 0; i < device.getAccounts().size(); i++) {
            // Initialize requests lists
            device.getAccounts().get(i).r = new ArrayList<>();
            device.getAccounts().get(i).auxR = new ArrayList<>();
            /////////////////////////////////////////////////////

            // Change position
            device.getAccounts().get(i).setPos(i);

            if (config.isBm()) {
                // Add request of delete + insert account [ auxR ]
                device.getAccounts().get(i).auxR.add("D");
                device.getAccounts().get(i).auxR.add("I A EUR,0");
                //////////////////////////////////////////////////
            } else {
                // Add request of delete + insert account [ r ]
                device.getAccounts().get(i).r.add("D");
                device.getAccounts().get(i).r.add("I A EUR,0");
                ///////////////////////////////////////////////
            }
        }

        // Broke ids of accounts
        brokeAccountIds();

        // Send request
        FirebaseDatabase.getInstance().getReference("devices/" + devId + "/accounts").setValue(device.accounts).addOnSuccessListener(aVoid -> {
            // Disconnect from SQLite
            disconnect();

            // Delete SQLite
            deleteFile(new File(activity.getDatabasePath("db.sqlite3").getPath()));

            // Build ids of accounts
            buildAccountIds();

            // Connect to SQLite
            connect();

            for (int i = 0; i < device.getAccounts().size(); i++) {
                // Insert default account to SQLite
                write().execSQL("insert into accounts (id, currency, money) values (?, ?, ?)",
                        new String[]{device.getAccounts().get(i).getId(),
                                "EUR",
                                String.valueOf(0)});
            }

            // Start account
            startAccount();
        });
    }

    @SuppressLint("Recycle")
    private void setDefaultAccount() {
        // Set id of first account
        Cursor c = read().rawQuery("select id from accounts limit 1", null);
        c.moveToFirst();
        def = c.getString(0);

        // Rewrite configuration
        rewriteConfig();
    }

    private void setDefaultAccount(String id) {
        // Set def
        def = id;

        // Rewrite configuration
        rewriteConfig();
    }
    ///////////////////////////////////////////////

    // Local files utilities
    public static int tryParseInt(String value, int defaultVal) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    private void rewriteConfig() {
        writeAllText(new File(activity.getFilesDir(), "config.txt"), configToString()); // [def],[fullScreen],[needBackup]
    }

    private String configToString() {
        return def + "," + (fullScreen ? "1" : "0") + "," + (needBackup ? "1" : "0");
    }

    private void stringToConfig(String config) {
        if (config != null) {
            String[] configStrings = config.split(",");
            if (configStrings.length == 3) {
                def = configStrings[0];
                fullScreen = tryParseInt(configStrings[1], 0) == 1;
                if (fullScreen) hideSystemUI(activity);
                needBackup = tryParseInt(configStrings[2].trim(), 0) == 1;
            }
        }
    }

    @Deprecated
    private void changeScreenSize() {
        fullScreen = !fullScreen;
        rewriteConfig();
        if (fullScreen) hideSystemUI(activity);
        else showSystemUI(activity);
        if (activity instanceof MainActivity) ((MainActivity) activity).resizeCalculatorButtons();
        new Handler().postDelayed(() -> pointEnabled = true, 750);
    }

    /////////////
    // OD DATA //
    /////////////
    // id
    public String ODId;
    // country code
    public int ODCountryCode = -1;
    // phone number
    public String ODPhoneNumber;
    // model
    public String ODModel;
    // shown
    public boolean ODShown = false;
    // premium
    public boolean ODPremium = false;
    /////////////////////////////////

    // b785f82f0a2dea8c,34,688614196,F1 POCOPHONE,1
    public void setODData(String ODData) {
        String[] ODDataStrings = ODData.split(",");
        if (ODDataStrings.length == 5) {
            ODId = ODDataStrings[0].length() <= 40 ? ODDataStrings[0] : null;
            try {
                ODCountryCode = Integer.parseInt(ODDataStrings[1]);
            } catch (NumberFormatException ignored) {
            }
            if (ODCountryCode != -1) {
                ODPhoneNumber = ODDataStrings[2].length() <= 10 ? ODDataStrings[2] : null;
                ODModel = ODDataStrings[3].length() <= 100 ? ODDataStrings[3] : null;
                try {
                    ODShown = Integer.parseInt(ODDataStrings[4].trim()) == 1;
                } catch (NumberFormatException ignored) {
                    deleteFile(new File(activity.getFilesDir(), "recoverDataFromMyOD.txt"));
                }
            } else
                deleteFile(new File(activity.getFilesDir(), "recoverDataFromMyOD.txt"));
        } else
            deleteFile(new File(activity.getFilesDir(), "recoverDataFromMyOD.txt"));
    }

    public void writeODData() {
        writeAllText(new File(activity.getFilesDir(), "recoverDataFromMyOD.txt"), ODDataToString());
    }

    private String ODDataToString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append(isNull(ODId) ? "" : ODId)
                .append(",")
                .append(ODCountryCode == -1 ? "" : ODCountryCode)
                .append(",")
                .append(isNull(ODPhoneNumber) ? "" : ODPhoneNumber)
                .append(",")
                .append(isNull(ODModel) ? "" : ODModel)
                .append(",")
                .append(ODShown ? "1" : "0");
        return stringBuilder.toString();
    }

    private boolean writeAllText(File file, String text) {
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            return writeAllText(outputStream, text);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private boolean writeAllText(OutputStream outputStream, String text) {
        OutputStreamWriter outputWriter = new OutputStreamWriter(outputStream);
        BufferedWriter bufferedWriter = new BufferedWriter(outputWriter);
        boolean success = false;

        try {
            bufferedWriter.write(text);
            success = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                bufferedWriter.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return success;
    }

    public String readAllText(File file) {
        try {
            FileInputStream inputStream = new FileInputStream(file);
            return readAllText(inputStream);
        } catch (Exception ex) {
            return null;
        }
    }

    private String readAllText(InputStream inputStream) {
        InputStreamReader inputreader = new InputStreamReader(inputStream);
        BufferedReader buffreader = new BufferedReader(inputreader);

        String line;
        StringBuilder text = new StringBuilder();

        try {
            while ((line = buffreader.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
        } catch (IOException e) {
            return null;
        }

        return text.toString();
    }

    public static String readLicense(Context context) {
        BufferedReader reader = null;

        String line;
        StringBuilder text = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(context.getAssets().open("NOTICE"), UTF_8));

            while ((line = reader.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
        } catch (IOException e) {
            //log the exception
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }
        return text.toString();
    }

    private void deleteFile(File file) {
        if (file.exists()) {
            file.delete();
        }
    }
    ///////////////////////////////////////////////////////////////

    // Firebase listener
    public void startListening() {
        tryToConnectToTheInternet(() -> {
            letStartListening();
            return null;
        });
    }

    private void letStartListening() {
        if (listenerCountry == null &&
                listenerConfig == null &&
                listenerDevice == null &&
                countryISO != null) {
            listenerCountry = FirebaseFirestore.getInstance().collection("countries").document(countryISO).addSnapshotListener((docCountry, error) -> {
                if (isDocReady(docCountry)) {
                    countryOn = docCountry.getBoolean("on");
                    auth = docCountry.getBoolean("auth");
                    if (countryOn) {
                        if (listenerConfig == null) {
                            __not_wait__();
                            onSuccessCountry();
                        } else __stopWaiting__();
                    } else {
                        __wait__("outside_the_system.json");
                    }
                } else {
                    __wait__("outside_the_system.json");
                }
            });
        } else {
            __wait__("outside_the_system.json");
        }
    }

    private void onSuccessCountry() {
        listenerConfig = FirebaseFirestore.getInstance().collection("config").document("es").addSnapshotListener((docConfig, error) -> {
            if (isDocReady(docConfig)) {
                config = docConfig.toObject(SystemConfig.class);
                if (isNotNull(config)) {
                    config.setV(docConfig.getLong("v").intValue());
                    if (config.isOn()) {
                        if (config != null && config.getV() != APP_VERSION) {
                            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)));
                            toast(activity, WARNING_TOAST, activity.getString(R.string.The_application_must_be_updated), Toast.LENGTH_SHORT);
                            __wait__("outside_the_system.json");
                        } else {
                            if (listenerDevice == null) {
                                __not_wait__();
                                onSuccessConfig();
                            } else __stopWaiting__();
                        }
                    } else {
                        __wait__("outside_the_system.json");
                    }
                } else {
                    __wait__("outside_the_system.json");
                }
            } else {
                __wait__("outside_the_system.json");
            }
        });
    }

    private void onSuccessConfig() {
        listenerDevice = FirebaseFirestore.getInstance().collection("devices").document(devId).addSnapshotListener((docDevice, error) -> {
            if (isDocReady(docDevice)) {
                bm = docDevice.getBoolean("bm");
                status = docDevice.getLong("status").intValue();

                if (!config.isBak() && !bm) deleteFile(new File(activity.getFilesDir(), "db.7z"));

                if (status == 1) {
                    if (!bm) {
                        __not_wait__();
                        if (FATAL_ERROR) setAccountsAsDefault();
                        else if (account == null) logIn();
                        else __stopWaiting__();
                    } else {
                        __wait__("backup.json");
                    }
                } else {
                    __wait__("outside_the_system.json");
                }
            } else {
                // Sign up
                signUp();
            }
        });
    }

    public void stopListening() {
        if (isNotNull(listenerCountry)) {
            listenerCountry.remove();
            listenerCountry = null;
        }

        if (isNotNull(listenerConfig)) {
            listenerConfig.remove();
            listenerConfig = null;
        }

        if (isNotNull(listenerDevice)) {
            listenerDevice.remove();
            listenerDevice = null;
        }
    }

    public void restartListening() {
        stopListening();
        startListening();
    }

    public void start() {
        restartListening();
    }

    public void stop() {
        stopListening();
    }

    public void resume() {
        /////////////   ////////////////
        //  MAIN  // -> // Calculator //
        /////////////   ////////////////
        if (activity.getClass() == MainActivity.class) {
            if (!FATAL_ERROR) {
                displayMoney();
                ((MainActivity) activity).resizeCalculatorButtons();
                if (ConCatActivity.isGoCon() || ConCatActivity.isGoCat()) {
                    Intent i = new Intent(activity, ConCatActivity.class);
                    putData(i);
                    i.putExtra("system", this);
                    going = true;
                    activity.startActivity(i);
                    if (ConCatActivity.isGoCon())
                        activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                    else
                        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                }
            } else {
                restartApp();
            }
        }
    }


    public void destroy() {
        if (activity instanceof MainActivity) {
            // Stop listening
            stopListening();

            // Account as null
            account = null;
        } else dismissDialog();
    }

    public void bye() {
        // Change content view
        activity.setContentView(R.layout.bye_layout);

        // Reset money values
        calcSymbol = '?';
        newValue = 0.00;
        newResult = 0.00;
        /////////////////

        // Stop listening
        stopListening();

        // Account as null
        account = null;

        // Finish app with delay
        Handler handler = new Handler();
        final Runnable r = activity::finish;
        handler.postDelayed(r, 250);
        //////////////////////////////////////
    }

    @SuppressLint("InflateParams")
    public void __wait__(String animation) {
        // Not waiting?
        if (!waiting) {
            // Waiting = true
            waiting = true;

            // Initialize dialog
            lottieDialog = buildDialog(lottieDialog, lottieView, 0, R.style.AppTheme_PopUpBlack, false);

            // Prepare lottie
            LottieAnimationView lottie = lottieView.findViewById(R.id.lottie);
            DisplayMetrics displayMetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int height = displayMetrics.heightPixels;
            int width = displayMetrics.widthPixels;
            lottie.setLayoutParams(new ConstraintLayout.LayoutParams(width, height));
            lottie.setAnimation(animation);
            ///////////////////////////////

            // Animate view
            Animation anim = AnimationUtils.loadAnimation(lottieView.getContext(), R.anim.fade_in);
            lottieView.startAnimation(anim);
            ////////////////////////////////

            activity.runOnUiThread(() -> {
                if (!activity.isFinishing()) {
                    try {
                        if (lottieView.getParent() == null) lottieDialog.setView(lottieView);
                        else {
                            lottieView = null;
                            lottieView = activity.getLayoutInflater().inflate(R.layout.dialog_lottie, null);
                            lottieDialog.setView(lottieView);
                        }

                        // Show dialog
                        lottieDialog.show();
                        lottieDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                        ///////////////////////////////////////////////////////////////////////////////////
                    } catch (WindowManager.BadTokenException e) {
                        toast(activity, WARNING_TOAST, activity.getString(R.string.Something_went_wrong), Toast.LENGTH_SHORT);
                    }
                }
            });
        }
    }

    private void __not_wait__() {
        if (waiting) dismissDialog(lottieDialog);
        waiting = false;
    }

    private void __stopWaiting__() {
        if (lottieDialog != null) {

            Animation anim = AnimationUtils.loadAnimation(lottieView.getContext(), R.anim.fade_out);

            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    dismissDialog(lottieDialog);
                    waiting = false;

                    if (listenerCountry == null &&
                            listenerConfig == null &&
                            listenerDevice == null &&
                            countryISO != null) {
                    } else {
                        if (config != null && !config.isOn()) {
                            __wait__("outside_the_system.json");
                        } else if (config != null && config.getV() != APP_VERSION) {
                            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)));
                            toast(activity, WARNING_TOAST, activity.getString(R.string.The_application_must_be_updated), Toast.LENGTH_SHORT);
                            __wait__("outside_the_system.json");
                        } else if (!countryOn) {
                            __wait__("outside_the_system.json");
                        } else if (status == 0) {
                            __wait__("outside_the_system.json");
                        } else if (bm) {
                            __wait__("backup.json");
                        } else if (activity != null && activity instanceof MainActivity) {
                            if (account == null) permitAccess();
                        }
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            lottieView.startAnimation(anim);
        } else
            waiting = false;
    }

    public void restartApp() {
        // Stop listening
        stopListening();

        // Reset calculator values
        calcSymbol = '?';
        newValue = 0.00;
        newResult = 0.00;
        /////////////////

        // Account as null
        account = null;

        // Refresh main activity
        ((MainActivity) activity).refreshActivity();
    }
    /////////////////////////////////

    ////////////
    // Log in //
    ////////////
    public void logIn() {
        FirebaseDatabase.getInstance().getReference("devices/" + devId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataDevice) {
                if (isDataReady(dataDevice)) {

                    // Get device data
                    device = getDevice(dataDevice);

                    // Build accounts ids
                    buildAccountIds();

                    // Catch ghost accounts
                    for (DeviceAccount da : device.getAccounts())
                        if ((isNotNull(da.r) && (da.r.size() == 1 && da.r.get(0).equals("D"))) ||
                                (isNotNull(da.auxR) && (da.auxR.size() == 1 && da.auxR.get(0).equals("D"))))
                            newGhostAccount(da.getId(), da.getPos());

                    // Remove ghost accounts
                    device.getAccounts().removeIf(Objects::isNull);

                    if (isNotNull(device)) {
                        if (device.isPassword() && Build.VERSION.SDK_INT >= 29) checkPassword();
                        else permitAccess();
                    } else __wait__("outside_the_system.json");
                } else __wait__("outside_the_system.json");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                toast(activity, System.WARNING_TOAST, activity.getString(R.string.Something_went_wrong), Toast.LENGTH_SHORT);
            }
        });
    }

    private Device getDevice(DataSnapshot dataDevice) {
        // Initialize device
        Device device = new Device();
        device.accounts = new ArrayList<>();
        ////////////////////////////////////

        ///////////////////////////
        // From FirebaseDatabase //
        ///////////////////////////
        //////////////
        // Accounts //
        //////////////
        for (DataSnapshot da : dataDevice.child("accounts").getChildren()) {
            // r
            ArrayList<String> r = new ArrayList<>();
            for (DataSnapshot dr : da.child("r").getChildren())
                r.add(dr.getValue(String.class));

            // id
            String id = da.child("id").getValue(String.class);

            // pos
            int pos = Integer.parseInt(da.child("pos").getValue().toString());

            // auxR
            ArrayList<String> auxR = new ArrayList<>();
            for (DataSnapshot dr : da.child("auxR").getChildren())
                auxR.add(dr.getValue(String.class));

            // Add account
            device.accounts.add(new DeviceAccount(auxR, id, pos, r));
        }

        // autR
        Object o = dataDevice.child("autR").getValue();
        device.setAutR(isNotNull(o) ? Integer.parseInt(o.toString()) : -1);

        // country
        device.setCountry(dataDevice.child("country").getValue().toString());

        // password
        device.setPassword(dataDevice.child("password").getValue(boolean.class));

        // phone
        device.setPhone(dataDevice.child("phone").getValue().toString());

        // premium
        device.setPremium(dataDevice.child("premium").getValue(boolean.class));
        ///////////////////////////////////////////////////////////////////////

        return device;
    }

    private void newGhostAccount(String id, int i) {
        if (ghostAccounts == null) ghostAccounts = new ArrayList<>();

        DeviceAccount ghostAccount = new DeviceAccount(id, i);
        ghostAccounts.add(ghostAccount);
        device.getAccounts().set(i, null);
    }

    private void permitAccess() {
        if (isNotNull(device.getAutR())) {
            switch (device.getAutR()) {
                case 0: // Delete
                    deleteData();
                    break;
                case 1: // Refresh
                    refreshData();
                    break;
                default: // Delete autR
                    FirebaseDatabase.getInstance().getReference("devices/" + devId + "/autR").removeValue().addOnSuccessListener(aVoid -> startAccessing());
                    break;
            }
        } else startAccessing();
    }

    private void startAccessing() {
        // Does the configuration file exist?
        File configFile = new File(activity.getFilesDir(), "config.txt");
        if (configFile.exists()) {
            stringToConfig(readAllText(configFile)); // [def],[fullScreen],[needBackup]
        } else {
            rewriteConfig(); // [def],[fullScreen],[needBackup]
        }

        // The device has accounts?
        if (device.getAccounts() != null && device.getAccounts().size() > 0) {

            // Does SQLite exist?
            if (doesTheDatabaseExist()) {
                // Connect to SQLite
                connect();

                // Validate if SQLite is corrupt
                if (isDatabaseCorrupt()) {
                    // Accounts as default
                    setAccountsAsDefault();
                } else {
                    // Non-accounts in SQLite
                    long accountsCount = count("accounts");
                    if (accountsCount == 0) {
                        // Restore data
                        restoreData();
                    } else {
                        // Start account
                        startAccount();
                    }
                }
            } else {
                // Restore data
                restoreData();
            }
        } else {
            // Delete local files
            deleteLocalFiles();
            buildNewAccount();
        }
    }
    /////////////////////////////////

    /////////////
    // Sign up //
    /////////////
    // Name
    public String name = "";
    //
    // Currency
    public String currency = "";
    //
    // Money
    public String numsBef = "0";
    public String numsAft = "00";
    public double money = 0.0;
    //
    // Phone number
    public String phoneNumber = "";
    //
    // Verification code
    public String verificationCode = "";
    ////////////////////////////////////

    public void signUp() {
        if (auth) {
            // Does the system have SMS permissions?
            permitSMS();
        } else {
            // Authentication denied
            __wait__("outside_the_system.json");

            // Wait to allow authentication
            waitForAuthentication(() -> {
                permitSMS();
                return null;
            });
        }
    }

    private void waitForAuthentication(Callable<Void> func) {

        Handler handler = new Handler();

        final Runnable r = new Runnable() {
            public void run() {
                if (isNetworkAvailable() && auth)
                    __not_wait__();

                if (!lottieDialog.isShowing()) {
                    handler.removeCallbacks(this);
                    try {
                        func.call();
                    } catch (Exception ignored) {
                        __wait__("outside_the_system.json");
                    }
                } else {
                    handler.postDelayed(this, 1000);
                }
            }
        };

        handler.postDelayed(r, 1000);
    }

    public void startAuthentication() { // LogIn or SignUp
        // Start LogInOrSignUpActivity
        Intent i = new Intent(activity, LogInOrSignUpActivity.class);
        i.putExtra("system", this);
        activity.startActivity(i);
        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        ////////////////////////////////////////////////////////////////////
    }

    public void startPortability() {
        /////////////////
        // Portability //
        /////////////////
        tryToConnectToTheInternet(() -> {
            FirebaseDatabase.getInstance().getReference("devices/" + ODId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // Delete local files
                    deleteLocalFiles();

                    if (isDataReady(snapshot)) {
                        if (ODId.equals(devId)) {
                            // Set fire store data
                            FirebaseFirestore.getInstance().collection("devices").document(devId).set(new HashMap<String, Object>() {{
                                    put("bm", false);
                                    put("status", 1);
                                }}
                            ).addOnSuccessListener(documentSnapshot -> {
                                // Go to loading activity
                                Intent i = new Intent(activity, MainActivity.class);
                                // Set the new task and clear flags
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                activity.startActivity(i);
                                activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                ////////////////////////////////////////////////////////////////////
                            });
                        } else {
                            // Get device data
                            device = getDevice(snapshot);

                            // Build accounts ids
                            buildAccountIds();

                            // Catch ghost accounts
                            for (DeviceAccount da : device.getAccounts())
                                if ((isNotNull(da.r) && (da.r.size() == 1 && da.r.get(0).equals("D"))) ||
                                        (isNotNull(da.auxR) && (da.auxR.size() == 1 && da.auxR.get(0).equals("D"))))
                                    newGhostAccount(da.getId(), da.getPos());

                            // Remove ghost accounts
                            device.getAccounts().removeIf(Objects::isNull);

                            // Transfer data
                            transferData();
                        }
                    } else {
                        // Init device
                        device = new Device();
                        device.setAutR(-1);
                        device.setCountry(countryISO);
                        device.setPassword(false);
                        device.setPhone(phoneNumber);
                        device.setPremium(false);

                        // Set values in Firebase
                        FirebaseFirestore.getInstance().collection("devices").document(devId).set(new HashMap<String, Object>() {{
                            put("bm", false);
                            put("status", 1);
                        }}).addOnSuccessListener(aVoid -> FirebaseDatabase.getInstance().getReference("devices/" + devId).setValue(device).addOnSuccessListener(aVoid1 -> {
                            String userId = device.getCountry() + "_" + device.getPhone();
                            FirebaseDatabase.getInstance().getReference("users/" + userId).setValue(devId).addOnSuccessListener(aVoid2 -> {
                                // Account as null
                                account = null;

                                // Go to loading activity
                                Intent i = new Intent(activity, MainActivity.class);
                                // Set the new task and clear flags
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                activity.startActivity(i);
                                activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                ////////////////////////////////////////////////////////////////////
                            });
                        }));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    toast(activity, INFO_TOAST, activity.getString(R.string.Something_went_wrong), Toast.LENGTH_SHORT);
                }
            });
            return null;
        });
    }

    private void allowPayPalPayment(String path, String productName) {
        if (config.isShop()) {
            // Payment toast
            toast(activity, INFO_TOAST, activity.getString(R.string.Make_sure_you_have_a_good_internet_connection_before_making_the_payment), Toast.LENGTH_SHORT);
            FirebaseDatabase.getInstance().getReference(path).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // Get value
                    double value = snapshot.getValue(double.class);

                    // Payment
                    PayPalPayment payment = new PayPalPayment(new BigDecimal(value), "EUR", productName,
                            PayPalPayment.PAYMENT_INTENT_SALE);
                    ///////////////////////////////////////////

                    // PayPal configuration
                    PayPalConfiguration payPalConfig = new PayPalConfiguration()
                            .environment(PayPalConfiguration.ENVIRONMENT_PRODUCTION)
                            .clientId(payPalClientId)
                            .merchantName(activity.getString(R.string.app_name));
                    /////////////////////////////////////////////////////////////

                    // Start activity
                    Intent intent = new Intent(activity, PaymentActivity.class);
                    intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, payPalConfig);
                    intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
                    activity.startActivityForResult(intent, payPalRequestCode);
                    ///////////////////////////////////////////////////////////
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    toast(activity, WARNING_TOAST, activity.getString(R.string.Something_went_wrong), Toast.LENGTH_SHORT);
                }
            });
        } else {
            toast(activity, WARNING_TOAST, activity.getString(R.string.It_has_not_been_possible_to_perform_this_action), Toast.LENGTH_SHORT);
        }
    }

    private void allowPayPalPayment(int value, String productName) {
        if (config.isShop()) {
            // Payment
            PayPalPayment payment = new PayPalPayment(new BigDecimal(value), "EUR", productName,
                    PayPalPayment.PAYMENT_INTENT_SALE);
            ///////////////////////////////////////////

            // PayPal configuration
            PayPalConfiguration payPalConfig = new PayPalConfiguration()
                    .environment(PayPalConfiguration.ENVIRONMENT_PRODUCTION)
                    .clientId(payPalClientId)
                    .merchantName(activity.getString(R.string.app_name));
            /////////////////////////////////////////////////////////////

            // Start activity
            Intent intent = new Intent(activity, PaymentActivity.class);
            intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, payPalConfig);
            intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
            activity.startActivityForResult(intent, payPalDonationRequestCode);
            ///////////////////////////////////////////////////////////////////
        } else {
            toast(activity, WARNING_TOAST, activity.getString(R.string.It_has_not_been_possible_to_perform_this_action), Toast.LENGTH_SHORT);
        }
    }

    public void setPremium() {
        set("devices/" + devId + "/premium", true);
        device.setPremium(true);
    }

    ///////////////////  //////////////////////////////////////
    // TRANSFER DATA //  // From Old Device -> To New Device //
    ///////////////////  //////////////////////////////////////
    ////////////
    // Values //
    ////////////
    // Fire store object
    Object oFireStoreDeviceData;

    // Fire database object
    Object oFireDatabaseDeviceData;
    ///////////////////////////////

    public void transferData() {
        tryToConnectToTheInternet(() -> {
            letTransferData();
            return null;
        });
    }

    public void letTransferData() {
        // Set from
        String from = ODId;

        // Set to
        String to = devId;

        // Get fire store data
        FirebaseFirestore.getInstance().collection("devices").document(from).get().addOnSuccessListener(documentSnapshot -> {
            // Get Firebase store data
            oFireStoreDeviceData =
                    isDataReady(documentSnapshot) ?
                        new HashMap<String, Object>() {{
                            put("bm", documentSnapshot.getBoolean("bm"));
                            put("status", documentSnapshot.getLong("status").intValue());
                        }}
                    :
                        new HashMap<String, Object>() {{
                            put("bm", false);
                            put("status", 1);
                        }};

            // Initialize destination file
            File db7z = new File(activity.getFilesDir(), "db.7z");

            // Download db.7z
            FirebaseStorage.getInstance().getReference("SRV/devs/" + from + "/db.7z").getFile(db7z)
                    // on success
                    .addOnSuccessListener(aVoid -> {
                        // Decompress db.7z
                        decompress();

                        // Restore data
                        new DataRestoration().restoreData(this, false);

                        // Clean requests queue
                        for (DeviceAccount da : device.accounts) { da.setR(new ArrayList<>()); da.setAuxR(new ArrayList<>()); }

                        // Broke accounts ids
                        brokeAccountIds();

                        // Get object
                        oFireDatabaseDeviceData = device;

                        if (new File(activity.getDatabasePath("db.sqlite3").getPath()).exists()) {
                            // Compress SQLite
                            compress();
                        }

                        // Upload db.7z
                        uploadDB7Z(to, () -> {
                            // Complete transfer
                            completeTransfer();
                            return null;
                        });
                    })
                    // on failure
                    .addOnFailureListener(e -> {
                        // Restore data
                        new DataRestoration().restoreData(this, false);

                        // Clean requests queue
                        for (DeviceAccount da : device.accounts) { da.setR(new ArrayList<>()); da.setAuxR(new ArrayList<>()); }

                        // Broke accounts ids
                        brokeAccountIds();

                        // Get object
                        oFireDatabaseDeviceData = device;

                        if (new File(activity.getDatabasePath("db.sqlite3").getPath()).exists()) {
                            // Compress SQLite
                            compress();
                        }

                        // Complete transfer
                        completeTransfer();
                    });

        });
    }

    private void uploadDB7Z(String devId, Callable<Void> func) {
        File db7z = new File(activity.getFilesDir(), "db.7z");
        try {
            FirebaseStorage.getInstance().getReference("SRV/devs/" + devId + "/db.7z").putStream(new FileInputStream(db7z)).addOnSuccessListener(taskSnapshot -> {
                try {
                    func.call();
                } catch (Exception ignored) {
                    __wait__("outside_the_system.json");
                }
            });
        } catch (FileNotFoundException ignored) {
            __wait__("outside_the_system.json");
        }

    }

    private void setFireData() {
        // Set fire database - Device
        FirebaseDatabase.getInstance().getReference("devices/" + devId).setValue(oFireDatabaseDeviceData).addOnSuccessListener(aVoid -> {
            // Set fire database - User
            FirebaseDatabase.getInstance().getReference("users/" + countryISO + "_" + phoneNumber).setValue(devId).addOnSuccessListener(aVoid1 -> {
                // Set fire store - Device
                FirebaseFirestore.getInstance().collection("devices").document(devId).set(oFireStoreDeviceData).addOnSuccessListener(aVoid2 -> {
                    // Non-waiting
                    waiting = false;

                    // Go to loading activity
                    Intent i = new Intent(activity, MainActivity.class);
                    // Set the new task and clear flags
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    activity.startActivity(i);
                    activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    ////////////////////////////////////////////////////////////////////
                });
            });
        });
    }

    public void completeTransfer() {
        // Delete fire storage data
        FirebaseStorage.getInstance().getReference("SRV/devs/" + ODId + "/db.7z").delete().addOnCompleteListener(voidTask -> {
            // Delete fire database data
            FirebaseDatabase.getInstance().getReference("devices/" + ODId).removeValue().addOnCompleteListener(voidTask1 -> {
                // Delete fire store data
                FirebaseFirestore.getInstance().collection("devices").document(ODId).delete().addOnCompleteListener(voidTask2 -> {
                    // Set fire data
                    setFireData();
                });
            });
        });
    }
    ///////////////////  ////////////
    // TRANSFER DATA //  // END... //
    ///////////////////  ////////////

    private String buildMessage(String verificationCode) {
        return activity.getString(R.string.this_is_the_Economic_Supervision_verification_code) +
                ": " + verificationCode;
    }

    private String buildVerificationCode() {
        return new RandomString(10, new SecureRandom(), RandomString.alphanum).nextString();
    }

    public void smsReceived() {
        deleteLocalFiles();
        initDevice();
    }

    public void initDevice() {
        tryToConnectToTheInternet(() -> {
            letInitDevice();
            return null;
        });
    }

    private void letInitDevice() {
        // Init device
        device = new Device();
        device.setAutR(-1);
        device.setCountry(countryISO);
        device.setPassword(false);
        device.setPhone(phoneNumber);
        device.setPremium(false);

        // Init DeviceAccount of device
        DeviceAccount da = new DeviceAccount(name.toLowerCase(), 0);

        // Init account
        account = new Account();
        account.setId(da.getId());
        account.setCurrency(currency);
        account.setMoney(money);
        ////////////////////////

        // Add request + account
        da.r = new ArrayList<>();
        da.r.add("I A " + account.getCurrency() + "," + account.getMoney());
        device.accounts = new ArrayList<>();
        device.accounts.add(da);
        ////////////////////////

        // Set values in Firebase
        FirebaseFirestore.getInstance().collection("devices").document(devId).set(new HashMap<String, Object>() {{
            put("bm", false);
            put("status", 1);
        }}).addOnSuccessListener(aVoid -> FirebaseDatabase.getInstance().getReference("devices/" + devId).setValue(device).addOnSuccessListener(aVoid1 -> {
            String userId = device.getCountry() + "_" + device.getPhone();
            FirebaseDatabase.getInstance().getReference("users/" + userId).setValue(devId).addOnSuccessListener(aVoid2 -> {
                // Build account id
                buildAccountIds();

                // Connect to SQLite
                connect();

                // Insert account in SQLite
                write().execSQL("insert into accounts (id, currency, money) values(?, ?, ?)", new String[]{device.accounts.get(0).getId(), account.getCurrency(), String.valueOf(account.getMoney())});

                // Set default account
                setDefaultAccount(account.getId());

                // Stop listening
                stopListening();

                // Account as null
                account = null;

                // Go to loading activity
                Intent i = new Intent(activity, MainActivity.class);
                // Set the new task and clear flags
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                activity.startActivity(i);
                activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                ////////////////////////////////////////////////////////////////////
            });
        }));
    }
    //////////////////////////////////

    ///////////////////////////
    // Permissions utilities //
    ///////////////////////////
    private void permitSMS() {
        ActivityCompat.requestPermissions(activity, new String[]{ Manifest.permission.INTERNET /* NO PERMITS REQUIRED */ }, 0);
    } // [ NO PERMITS REQUIRED ] | SIGN UP -> 0

    private void permitCONTACTS() {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.RECORD_AUDIO}, 1);
    } // [ READ_CONTACTS | RECORD_AUDIO ] | TRANSACTIONS -> 1

    private void permitLOOCATION_CAT() {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
    } // [ ACCESS_FINE_LOCATION ] | CATEGORIES -> 2

    private void permitLOOCATION_CON() {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 3);
    } // [ ACCESS_FINE_LOCATION ] | CONCEPTS -> 3

    private void permitRECORD_AUDIO() {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECORD_AUDIO}, 4);
    } // [ RECORD_AUDIO ] | MOVEMENTS -> 4

    private String getMessage(int requestCode) {
        String msg = "";
        switch (requestCode) {
            case 0: // [ NO PERMITS REQUIRED ] | SIGN UP
                msg = "";
                break;
            case 1: // [ READ_CONTACTS | RECORD_AUDIO ] | TRANSACTIONS
                msg = activity.getString(R.string.Microphone_and_contact_read_permissions_are_required_to_perform_and_filter_transactions);
                break;
            case 2: // [ ACCESS_FINE_LOCATION ] | CATEGORIES
            case 3: // [ ACCESS_FINE_LOCATION ] | CONCEPTS
                msg = activity.getString(R.string.Location_permits_are_required_to_perform_movements);
                break;
            case 4: // [ RECORD_AUDIO ] | MOVEMENTS
                msg = activity.getString(R.string.Microphone_permissions_are_required_to_filter_movements);
                break;
            case 5: // [ READ_CONTACTS ] | CONTACTS
                msg = activity.getString(R.string.Contact_read_permissions_are_required_to_perform_this_action);
                break;
        }
        return msg;
    }

    public void allowPermissionsManually(Activity activity, int requestCode) {

        SwipeButton swipeButton = infoView.findViewById(R.id.swipeBtn);
        swipeButton.setActivated(false);

        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        activity.startActivity(intent);

        toast(activity, INFO_TOAST, getMessage(requestCode), Toast.LENGTH_SHORT);
    }

    public void explainTheReasonForThePermissions(int requestCode) {
        // Initialize dialog
        infoDialog = buildDialog(infoDialog, infoView, 0, R.style.AppTheme_PopUpBlack, false);

        // Set reason
        TextView txtvReason = infoView.findViewById(R.id.txtvMessage);
        txtvReason.setText(getMessage(requestCode));

        // Swipe listener
        SwipeButton swipeButton = infoView.findViewById(R.id.swipeBtn);
        swipeButton.setOnStateChangeListener(active -> {
            if (active) {
                switch (requestCode) {
                    case 0:
                        permitSMS();
                        break;
                    case 1:
                        permitCONTACTS();
                        break;
                    case 2:
                        permitLOOCATION_CAT();
                        break;
                    case 3:
                        permitLOOCATION_CON();
                        break;
                    case 4:
                        permitRECORD_AUDIO();
                        break;
                }
            }
        });

        // Animate view
        infoView.startAnimation(AnimationUtils.loadAnimation(infoView.getContext(), R.anim.fade_in));

        // Show dialog
        showDialog(infoDialog);
    }

    public static ArrayList<Designer> getDesigners(Context context, boolean images) {
        ArrayList<Designer> designers = new ArrayList<>();
        if (images) {
            designers.add(Images.by(context, Designers.FREEPIK));
            designers.add(Images.by(context, Designers.PIXELPERFECT));
            designers.add(Images.by(context, Designers.THOSEICONS));
            designers.add(Images.by(context, Designers.SMASHICONS));
            designers.add(Images.by(context, Designers.SRIP));
            designers.add(Images.by(context, Designers.STOCKIO));
            designers.add(Images.by(context, Designers.APIEN));
            designers.add(Images.by(context, Designers.DAVEGANDY));
            designers.add(Images.by(context, Designers.KERISMAKER));
            designers.add(Images.by(context, Designers.ROUNDICONS));
            designers.add(Images.by(context, Designers.GOOGLE));
        } else {
            designers.add(Animations.by(context, Designers.VIK4GRAPHIC));
            designers.add(Animations.by(context, Designers.LOTTIEFILEZ));
            //designers.add(Animations.by(context, Designers.SAMYMENAY));
            designers.add(Animations.by(context, Designers.LOTTIEFILES));
            designers.add(Animations.by(context, Designers.V3UT3N7A2O));
            designers.add(Animations.by(context, Designers.KOBRO));
            designers.add(Animations.by(context, Designers.EMCKEE));
            designers.add(Animations.by(context, Designers.COLORSTREAK));
            designers.add(Animations.by(context, Designers.USER90710));
            designers.add(Animations.by(context, Designers.USER762847));
            designers.add(Animations.by(context, Designers.TUUA9XVVX2));
            designers.add(Animations.by(context, Designers.SPLASHANIMATION));
            designers.add(Animations.by(context, Designers.NIKHITA));
            designers.add(Animations.by(context, Designers.TANJIL));
        }
        return designers;
    }

    public static void loadUrl(Context context, String url) {
        Uri link = Uri.parse(url);
        Intent i = new Intent(Intent.ACTION_VIEW, link);
        context.startActivity(i);
    }

    public static ArrayList<String> getExamples(Context context, int pos, boolean movements) {
        int min, max, rand, otherRand;
        ArrayList<String> examples;
        if (movements) {
            switch (pos) {
                case 0: // Categorical / Conceptual
                    return new ArrayList<>(Arrays.asList(context.getResources().getStringArray(R.array.movements_voice_assistant_examples_concat)));
                case 1: // From category [category name]
                    min = 1; max = 9; rand = getRandom(min, max);
                    // Categories
                    String[] cats = context.getResources().getStringArray(R.array.categories);

                    // Category
                    String category = context.getResources().getStringArray(R.array.categories)[rand].split("\\|")[0];

                    // SubCategory
                    min = 0;
                    max = cats[rand].split("\\|")[1].split(",").length - 1;
                    String subCategory = cats[rand].split("\\|")[1].split(",")[getRandom(min, max)];

                    // Build examples
                    examples = new ArrayList<>(Arrays.asList(context.getResources().getStringArray(R.array.movements_voice_assistant_examples_from_category)));
                    examples.set(0, examples.get(0).replace("X", category));
                    examples.set(1, examples.get(1).replace("X", subCategory));

                    // Return examples
                    return examples;
                case 2: // Of the concept [name of the concept]
                    return new ArrayList<>(Arrays.asList(context.getResources().getStringArray(R.array.movements_voice_assistant_examples_from_concept)));
                case 3: // Positive / Negative
                    return new ArrayList<>(Arrays.asList(context.getResources().getStringArray(R.array.movements_voice_assistant_examples_pos_neg)));
                case 4: // Higher / Lower than [value]
                    min = 1; max = 1000;

                    // Calc rands
                    rand = getRandom(min, max);
                    do otherRand = getRandom(min, max);  while (otherRand == rand);

                    // Build examples
                    examples = new ArrayList<>(Arrays.asList(context.getResources().getStringArray(R.array.movements_voice_assistant_examples_higher_lower)));
                    examples.set(0, examples.get(0).replace("X", String.valueOf(rand)));
                    examples.set(1, examples.get(1).replace("X", String.valueOf(otherRand)));

                    // Return examples
                    return examples;
                case 5: // Between [value] and [value]
                    min = 1; max = 1000;

                    // Calc rands
                    rand = getRandom(min, max);
                    do otherRand = getRandom(min, max);  while (otherRand == rand);

                    // Build examples
                    examples = new ArrayList<>(Arrays.asList(context.getResources().getStringArray(R.array.movements_voice_assistant_examples_between_value)));
                    examples.set(0, examples.get(0).replace("X", String.valueOf(rand)).replace("Y", String.valueOf(otherRand)));

                    // Return examples
                    return examples;
                case 6: // After / Before [date / period]
                    return new ArrayList<>(Arrays.asList(context.getResources().getStringArray(R.array.movements_voice_assistant_examples_after_before)));
                case 7: // Between [x date / period] and [x date / period]
                    return new ArrayList<>(Arrays.asList(context.getResources().getStringArray(R.array.movements_voice_assistant_examples_between_dates)));
                case 8: // X date / period
                    return new ArrayList<>(Arrays.asList(context.getResources().getStringArray(R.array.movements_voice_assistant_examples_date_period)));
                case 9: // More / Less than [x time]
                    return new ArrayList<>(Arrays.asList(context.getResources().getStringArray(R.array.movements_voice_assistant_examples_more_less)));
                case 10: // [x time] ago
                    return new ArrayList<>(Arrays.asList(context.getResources().getStringArray(R.array.movements_voice_assistant_examples_ago)));
                case 11: // Exactly [x time] ago
                    return new ArrayList<>(Arrays.asList(context.getResources().getStringArray(R.array.movements_voice_assistant_examples_exactly_ago)));
                case 12: // In [street names, locality, province, autonomy or country]
                    return new ArrayList<>(Arrays.asList(context.getResources().getStringArray(R.array.movements_voice_assistant_examples_in)));
            }
        } else {
            switch (pos) {
                case 0: // Individual / Group
                    return new ArrayList<>(Arrays.asList(context.getResources().getStringArray(R.array.transactions_voice_assistant_examples_ind_grp)));
                case 1: // Positive / Negative
                    return new ArrayList<>(Arrays.asList(context.getResources().getStringArray(R.array.transactions_voice_assistant_examples_pos_neg)));
                case 2: // Higher / Lower than [value]
                    min = 1; max = 1000;

                    // Calc rands
                    rand = getRandom(min, max);
                    do otherRand = getRandom(min, max);  while (otherRand == rand);

                    // Build examples
                    examples = new ArrayList<>(Arrays.asList(context.getResources().getStringArray(R.array.transactions_voice_assistant_examples_higher_lower)));
                    examples.set(0, examples.get(0).replace("X", String.valueOf(rand)));
                    examples.set(1, examples.get(1).replace("X", String.valueOf(otherRand)));

                    // Return examples
                    return examples;
                case 3: // Between [value] and [value]
                    min = 1; max = 1000;

                    // Calc rands
                    rand = getRandom(min, max);
                    do otherRand = getRandom(min, max);  while (otherRand == rand);

                    // Build examples
                    examples = new ArrayList<>(Arrays.asList(context.getResources().getStringArray(R.array.transactions_voice_assistant_examples_between_value)));
                    examples.set(0, examples.get(0).replace("X", String.valueOf(rand)).replace("Y", String.valueOf(otherRand)));

                    // Return examples
                    return examples;
                case 4: // After / Before [date / period]
                    return new ArrayList<>(Arrays.asList(context.getResources().getStringArray(R.array.transactions_voice_assistant_examples_after_before)));
                case 5: // Between [x date / period] and [x date / period]
                    return new ArrayList<>(Arrays.asList(context.getResources().getStringArray(R.array.transactions_voice_assistant_examples_between_dates)));
                case 6: // X date / period
                    return new ArrayList<>(Arrays.asList(context.getResources().getStringArray(R.array.transactions_voice_assistant_examples_date_period)));
                case 7: // More / Less than [x time]
                    return new ArrayList<>(Arrays.asList(context.getResources().getStringArray(R.array.transactions_voice_assistant_examples_more_less)));
                case 8: // [x time] ago
                    return new ArrayList<>(Arrays.asList(context.getResources().getStringArray(R.array.transactions_voice_assistant_examples_ago)));
                case 9: // Exactly [x time] ago
                    return new ArrayList<>(Arrays.asList(context.getResources().getStringArray(R.array.transactions_voice_assistant_examples_exactly_ago)));
                case 10: // From [friends names]
                    return new ArrayList<>(Arrays.asList(context.getResources().getStringArray(R.array.transactions_voice_assistant_examples_in)));
            }
        }
        return null;
    }

    private static int getRandom(int min, int max) {
        return new Random().nextInt((max - min) + 1) + min;
    }
}
