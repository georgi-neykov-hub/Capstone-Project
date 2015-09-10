package com.neykov.podcastportal.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.design.widget.Snackbar;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.neykov.podcastportal.R;

public class ViewUtils {

    @SuppressLint("NewApi")
    public static void setElevation(View view, float elevationPixels){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.setElevation(elevationPixels);
        }
    }

    @SuppressLint("NewApi")
    public static void setElevation(View view, @DimenRes int elevationDimen, Resources resources){
        setElevation(view, resources.getDimension(elevationDimen));
    }

    @SuppressWarnings("deprecation")
    public static Drawable getDrawable(@DrawableRes int resId, Resources resources, Resources.Theme theme){
        if(Build.VERSION.SDK_INT< Build.VERSION_CODES.LOLLIPOP) {
            return resources.getDrawable(resId);
        }else{
            return resources.getDrawable(resId, theme);
        }
    }

    public static void hideSoftwareKeyboard(Activity activity){
        InputMethodManager imm =
                (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);

        View view = activity.getCurrentFocus();
        if (view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static int getThemeAttribute (Resources.Theme theme, int themeAttr) {
        final TypedValue value = new TypedValue();
        theme.resolveAttribute(themeAttr, value, true);
        return value.resourceId;
    }

    public static Snackbar getNoNetworkSnackbar(Context context, View hostView){
        return Snackbar.make(hostView, R.string.label_no_network, Snackbar.LENGTH_LONG)
                .setAction(R.string.action_settings, v -> {
                    context.startActivity(new Intent(Settings.ACTION_SETTINGS)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                });
    }
}
