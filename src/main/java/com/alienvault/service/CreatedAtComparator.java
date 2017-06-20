package com.alienvault.service;

import com.alienvault.json.Issue;

import java.util.Comparator;

public class CreatedAtComparator implements Comparator<Issue>{

    @Override
    public int compare(Issue o1, Issue o2) {
        return o1.getCreatedDate().compareTo(o2.getCreatedDate());
    }
}
