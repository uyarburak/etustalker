package com.okapi.stalker.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.okapi.stalker.R;
import com.okapi.stalker.activity.SectionActivity;
import com.okapi.stalker.activity.StudentActivity;
import com.okapi.stalker.data.FriendsDataBaseHandler;
import com.okapi.stalker.data.MainDataBaseHandler;
import com.okapi.stalker.data.storage.model.Interval;
import com.okapi.stalker.data.storage.model.Person;
import com.okapi.stalker.data.storage.model.Section;
import com.okapi.stalker.data.storage.model.Student;
import com.okapi.stalker.fragment.adapters.MySectionAdapter;
import com.okapi.stalker.util.ColorGenerator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;


public class ProgramFragment extends Fragment{
    private MainDataBaseHandler db;
    private View rootView;
    private int maxHour;
    private Person owner;

    public ProgramFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        maxHour = -1;
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            ColorGenerator colorGenerator = ColorGenerator.MATERIAL;
            rootView = inflater.inflate(R.layout.fragment_program, container, false);
            db = new MainDataBaseHandler(getActivity());
            int pixels = dpToPx(getContext(), 50);
            int pixels10 = dpToPx(getContext(), 10);
            int pixels2 = dpToPx(getContext(), 2);

            List<MyButton> buttons = new ArrayList<MyButton>(20);
            String[] colors = {"#63b526", "#009AE3", "#f584d4", "#ff7800",
                    "#f74448", "#c3903f", "#a5de5b"};
            int colorIndex = 0;
            Boolean hasInterval = null;
            for (Section sectionOnlyId : owner.getSections()) {
                final Section section = db.getSection(sectionOnlyId.getId());
                Set<Interval> intervals = db.getIntervalsOfSection(section.getId());
                for (Interval interval: intervals) {
                    hasInterval = true;
                    MyButton button = new MyButton(
                            interval.getDay() * 13 + interval.getHour(),
                            section.getId(),
                            String.format("%s (%s)", section.getCourse().getCode(), interval.getRoom()),
                            colors[colorIndex]
                    );
                    if(interval.getHour() > maxHour){
                        maxHour = interval.getHour();
                    }
                    buttons.add(button);
                }
                if(++colorIndex == colors.length){
                    colorIndex = 0;
                }
                if(intervals.isEmpty() && owner instanceof Student && section.getCourse().getCode().matches("OEG.+00") && owner.getSections().size() == 1){
                    rootView.findViewById(R.id.blockView).setVisibility(View.VISIBLE);
                    rootView.findViewById(R.id.blockView).bringToFront();
                    rootView.findViewById(R.id.internImage).setVisibility(View.VISIBLE);
                    rootView.findViewById(R.id.internImage).bringToFront();
                    System.out.println("yeterrr : " + ((Student) owner).getName() + " xd " + section.getCourse().getCode());
                    return rootView;
                }
            }
            if(hasInterval == null){
                Set<Section> detailedSections = new HashSet<Section>();
                for (Section sectionOnlyId : owner.getSections()) {
                    detailedSections.add(db.getSectionWithoutStudents(sectionOnlyId.getId()));
                }

                rootView.findViewById(R.id.blockView).setVisibility(View.VISIBLE);
                rootView.findViewById(R.id.blockView).bringToFront();
                ListView sectionList = (ListView) rootView.findViewById(R.id.noIntervalSectionList);
                if(owner.getSections().isEmpty()){
                    ImageView imageView = (ImageView) rootView.findViewById(R.id.no_course_image);
                    imageView.setVisibility(View.VISIBLE);
                    imageView.bringToFront();
                }else{
                    sectionList.setVisibility(View.VISIBLE);
                    sectionList.bringToFront();
                    sectionList.setAdapter(new MySectionAdapter(getActivity(), detailedSections));
                    sectionList.setOnItemClickListener(
                            new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> a, View v, int position, long l) {
                                    Intent intent = new Intent(getActivity(), SectionActivity.class);
                                    intent.putExtra("section", ((Section)a.getAdapter().getItem(position)).getId());
                                    getActivity().startActivity(intent);
                                }
                            });
                    rootView.findViewById(R.id.noIntervalSectionList).bringToFront();
                }

            }else{
                Map<Integer, MyButton> buttonMap =  new HashMap<Integer, MyButton>();
                for (MyButton button : buttons){
                    if(buttonMap.containsKey(button.index)){
                        MyButton button1 = buttonMap.get(button.index);
                        button1.title = button1.title.concat(" and ").concat(button.title);
                        button1.color = "black";
                        button1.section2 = button.sectionId;
                    }else{
                        buttonMap.put(button.index, button);
                    }
                }
                MyButton defaultButton = new MyButton();

                LinearLayout linearLayout =
                        (LinearLayout) rootView.findViewById(R.id.calendarSplitterRelativeLayout);

                LinearLayout.LayoutParams prm = new LinearLayout.LayoutParams(
                        0, LinearLayout.LayoutParams.MATCH_PARENT, 2);
                prm.setMargins(pixels2, 0, 0, 0);

                RelativeLayout relativeLayout = null;
                for (int i = 0; i < 78; i++) {
                    final MyButton myButton = buttonMap.containsKey(i) ? buttonMap.get(i) : defaultButton;
                    final TextView button = new TextView(getActivity());

                    final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0, 0, 0, pixels10);
                    if (i % 13 != 0) {
                        params.addRule(RelativeLayout.BELOW, i);
                    } else {
                        relativeLayout = new RelativeLayout(getActivity());
                        relativeLayout.setLayoutParams(prm);
                        linearLayout.addView(relativeLayout);
                    }

                    button.setLayoutParams(params);
                    button.setHeight(pixels);
                    button.setGravity(Gravity.CENTER);
                    button.setId(i + 1);
                    if(myButton.sectionId == null || myButton.color.equals("black"))
                        button.setBackgroundColor(Color.parseColor(myButton.color));
                    else
                        button.setBackgroundColor(colorGenerator.getColorForSections(myButton.sectionId));
                    //button.setBackgroundColor(Color.parseColor(myButton.color));
                    final int index = i;
                    if(myButton.title == null){
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                freeFriendsDialog(index);
                            }
                        });
                    }else{
                        button.setText(myButton.title);
                        button.setTextSize(13);
                        button.setTextColor(Color.WHITE);
                        if(myButton.color.equals("black")){
                            final String[] parts = myButton.title.split(" and ");
                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    System.out.println(myButton.title);
                                    if(button.getText().toString().startsWith(parts[0])){
                                        button.setText(parts[1]);
                                    }else{
                                        button.setText(parts[0]);
                                    }
                                }
                            });
                            button.setOnLongClickListener(new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View v) {
                                    sezinBug(parts, new int[]{myButton.sectionId, myButton.section2});
                                    return false;
                                }
                            });
                        }else{
                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getActivity(), SectionActivity.class);
                                    intent.putExtra("section", myButton.sectionId);
                                    getActivity().startActivity(intent);
                                }
                            });
                            button.setOnLongClickListener(new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View v) {
                                    freeFriendsDialog(index);
                                    return false;
                                }
                            });
                        }
                    }
                    //button.setId(i + 1);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        button.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    }
                    button.bringToFront();
                    relativeLayout.addView(button);
                }
            }

            rootView.findViewById(R.id.refreshLinearLayout).setVisibility(View.INVISIBLE);
            rootView.findViewById(R.id.refreshView).setVisibility(View.INVISIBLE);


            final LinearLayout currentTimeLine =
                    (LinearLayout) rootView.findViewById(R.id.currentTimeMarkerLinearLayout);


            final RelativeLayout.LayoutParams params =
                    new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);
            int[] days = {
                    R.id.mondayTextView,
                    R.id.tuesdayTextView,
                    R.id.wednesdayTextView,
                    R.id.thursdayTextView,
                    R.id.fridayTextView,
                    R.id.saturdayTextView,
            };
            Time time = new Time();
            time.setToNow();

            if(time.weekDay != 0){
                TextView textView = (TextView) rootView.findViewById(days[time.weekDay-1]);
                textView.setTextColor(Color.parseColor("#FF4081"));
            }
            Timer timer = new Timer();
            TimerTask updateClock = new TimerTask() {
                @Override
                public void run() {
                    if(getActivity() != null){
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Time line initializing
                                Time time = new Time();
                                time.setToNow();
                                if(time.hour < 21){
                                    currentTimeLine.setVisibility(View.VISIBLE);
                                    int minutes = (time.hour * 60) + time.minute - 510;

                                    params.setMargins(0, dpToPx(getActivity(), minutes), 0, 0);
                                    currentTimeLine.setLayoutParams(params);
                                }else{
                                    currentTimeLine.setVisibility(View.INVISIBLE);
                                }
                            }
                        });
                    }

                }
            };
            timer.scheduleAtFixedRate(updateClock, 0, 60000);
        }

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_program, menu);
    }

    private int dpToPx(Context context, int dp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
    }

    private void sezinBug(String[] courses, final int[] courseIds){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setTitle("CHOOSE");
        dialogBuilder.setItems(courses, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                Intent intent = new Intent(getActivity(), SectionActivity.class);
                intent.putExtra("section", courseIds[item]);
                getActivity().startActivity(intent);
            }
        });
        //Create alert dialog object via builder
        AlertDialog alertDialogObject = dialogBuilder.create();
        //Show the dialog
        alertDialogObject.show();
    }

    private void freeFriendsDialog(int index){
        final Collator coll = Collator.getInstance(new Locale("tr", "TR"));
        coll.setStrength(Collator.PRIMARY);
        final Map<String, Student> freeGuys = new TreeMap<String, Student>(new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                return coll.compare(lhs, rhs);
            }
        });
        FriendsDataBaseHandler dbFriends = new FriendsDataBaseHandler(getActivity());
        List<String> friends = dbFriends.getAllFriends();
        etiket:
        for (String key: friends){
            Student friend = db.getStudent(key);
            for(Section section: friend.getSections()){
                Set<Interval> intervals = db.getIntervalsOfSection(section.getId());
                for (Interval interval: intervals){
                    int indeks = (interval.getDay() * 13) + interval.getHour();
                    if(indeks == index)
                        continue etiket;
                }
            }
            freeGuys.put(friend.getName(), friend);
        }

        //Create sequence of items
        final CharSequence[] freeGuyNames = freeGuys.keySet().toArray(new String[freeGuys.size()]);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setTitle(getString(R.string.free_friends));
        dialogBuilder.setItems(freeGuyNames, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                Student friend = freeGuys.get(freeGuyNames[item].toString());  //Selected item in listview
                Intent intent = new Intent(getActivity(), StudentActivity.class);
                intent.putExtra("student", (Serializable) friend);
                getActivity().startActivity(intent);
            }
        });
        //Create alert dialog object via builder
        AlertDialog alertDialogObject = dialogBuilder.create();
        //Show the dialog
        alertDialogObject.show();
    }
    public void setOwner(Person owner) {
        this.owner = owner;
    }
    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        this.owner = (Person) args.getSerializable("owner");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id){
            case R.id.action_share_program:
                if(maxHour == -1){
                    Toast.makeText(getContext(), getString(R.string.nothing_to_send), Toast.LENGTH_SHORT).show();
                    break;
                }
                final LinearLayout currentTimeLine =
                        (LinearLayout) rootView.findViewById(R.id.currentTimeMarkerLinearLayout);
                int visibility = currentTimeLine.getVisibility();
                currentTimeLine.setVisibility(View.GONE);
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                RelativeLayout root = (RelativeLayout) inflater.inflate(R.layout.fragment_program, null); // activity_main is UI(xml) file we used in our Activity class. FrameLayout is root view of my UI(xml) file.
                root.setDrawingCacheEnabled(true);
                Bitmap bitmap = getBitmapFromView(getActivity().getWindow().findViewById(R.id.dayLabelsLinearLayout)); // here give id of our root layout (here its my FrameLayout's id)
                Bitmap bitmap2 = getBitmapFromView(getActivity().getWindow().findViewById(R.id.dividerView)); // here give id of our root layout (here its my FrameLayout's id)
                Bitmap bitmap3 = getBitmapFromView(getActivity().getWindow().findViewById(R.id.calendarScrollView), dpToPx(getActivity(), 60*(maxHour+1))); // here give id of our root layout (here its my FrameLayout's id)
                Bitmap combined = combineImages(bitmap, bitmap2);
                combined = combineImages(combined, bitmap3);
                currentTimeLine.setVisibility(visibility);
                // save bitmap to cache directory
                try {

                    File cachePath = new File(getActivity().getCacheDir(), "images");
                    cachePath.mkdirs(); // don't forget to make the directory
                    FileOutputStream stream = new FileOutputStream(cachePath + "/image.png"); // overwrites this image every time
                    combined.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    stream.close();

                    File newFile = new File(cachePath, "image.png");
                    Uri contentUri = FileProvider.getUriForFile(getActivity(), "com.okapi.stalker", newFile);

                    if (contentUri != null) {
                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
                        shareIntent.setDataAndType(contentUri, getContext().getContentResolver().getType(contentUri));
                        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                        startActivity(Intent.createChooser(shareIntent, getString(R.string.choose_an_app)));
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
        return true;
    }


    private Bitmap combineImages(Bitmap c, Bitmap s) { // can add a 3rd parameter 'String loc' if you want to save the new image - left some code to do that at the bottom
        Bitmap cs = null;

        int width = c.getWidth();
        int height = c.getHeight() + s.getHeight();

        cs = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas comboImage = new Canvas(cs);

        comboImage.drawBitmap(c, 0f, 0f, null);
        comboImage.drawBitmap(s, 0f, c.getHeight(), null);

        return cs;
    }



    private Bitmap getBitmapFromView(View view) {
        return getBitmapFromView(view, view.getHeight());
    }
    private Bitmap getBitmapFromView(View view, int height) {

        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return returnedBitmap;
    }

}
class MyButton{
    Integer index;
    Integer sectionId;
    String title;
    String color;
    Integer section2;

    public MyButton() {
        super();
        color = "#ececec";
    }
    public MyButton(Integer index, Integer sectionId, String title, String color) {
        super();
        this.index = index;
        this.sectionId = sectionId;
        this.title = title;
        this.color = color;
    }
}