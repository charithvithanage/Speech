package com.google.cloud.android.speech;

import android.content.Context;
import android.content.Intent;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Utils {

    public static void startNewActivity(Context context, Class activity) {
        Intent intent = new Intent(context, activity);
        context.startActivity(intent);
    }

    //Sort card list
    public static List<SampleObject> sortCardList(List<SampleObject> lsit) {

        Collections.sort(lsit, new Comparator<SampleObject>() {
            public int compare(SampleObject obj1, SampleObject obj2) {
                // ## Ascending order
                return Integer.valueOf((int) obj1.getCount()).compareTo((int) obj2.getCount()); // To compare string values
                // return Integer.valueOf(obj1.empId).compareTo(obj2.empId); // To compare integer values

                // ## Descending order
                // return obj2.firstName.compareToIgnoreCase(obj1.firstName); // To compare string values
                // return Integer.valueOf(obj2.empId).compareTo(obj1.empId); // To compare integer values
            }
        });
        return lsit;
    }

}
