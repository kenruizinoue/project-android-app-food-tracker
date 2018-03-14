package com.apps.yecotec.fridgetracker.utils;

import android.content.Context;

import com.apps.yecotec.fridgetracker.data.Section;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kenruizinoue on 9/12/17.
 */

public class SectionUtils {

    public static Section[] filterSectionById(int id, Context context) {
        Section[] sections = new DBUtils(context).getAllSectionsArray();

        List<Section> sectionList = new ArrayList<>();

        for (int i = 0; i <sections.length ; i++) {
            if(id == sections[i].sectionId) {
                sectionList.add(sections[i]);
            }
        }

        sections = sectionList.toArray(new Section[sectionList.size()]);

        return sections;
    }
}
