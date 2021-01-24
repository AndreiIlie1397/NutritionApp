package com.example.nutritionapp.Utility;

import com.example.nutritionapp.Main_window.General;
import com.example.nutritionapp.Main_window.Jurnal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class SectionPagerAdapter extends FragmentPagerAdapter {

    public SectionPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                General general_tab = new General();
                return general_tab;
            case 1:
                Jurnal jurnal_tab = new Jurnal();
                return  jurnal_tab;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "General";
            case 1:
                return "Jurnal";
            default:
                return null;
        }
    }
}
