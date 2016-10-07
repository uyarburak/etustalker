package com.eggheadgames.siren;

import android.app.Activity;
import android.app.Dialog;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.lang.ref.WeakReference;

public class SirenAlertWrapper {

    private final WeakReference<Activity> mActivityRef;
    private final ISirenListener mSirenListener;
    private final SirenAlertType mSirenAlertType;
    private final String mMinAppVersion;
    private final SirenSupportedLocales mLocale;
    private final SirenHelper mSirenHelper;
    private int mTheme;

    public SirenAlertWrapper(Activity activity, ISirenListener sirenListener, SirenAlertType sirenAlertType,
                             String minAppVersion, SirenSupportedLocales locale, SirenHelper sirenHelper) {
        this.mSirenListener = sirenListener;
        this.mSirenAlertType = sirenAlertType;
        this.mMinAppVersion = minAppVersion;
        this.mLocale = locale;
        this.mSirenHelper = sirenHelper;
        this.mActivityRef = new WeakReference<>(activity);
    }

    @SuppressWarnings("unused")
    public SirenAlertWrapper(Activity activity, ISirenListener sirenListener, SirenAlertType sirenAlertType,
                             String minAppVersion, SirenSupportedLocales locale, SirenHelper sirenHelper, int theme) {
        this(activity, sirenListener, sirenAlertType, minAppVersion, locale, sirenHelper);
        this.mTheme = theme;
    }

    public void show() {
        Activity activity = mActivityRef.get();
        if (activity == null) {
            if (mSirenListener != null) {
                mSirenListener.onError(new NullPointerException("activity reference is null"));
            }
        } else if (Build.VERSION.SDK_INT >= 17 && !activity.isDestroyed() || Build.VERSION.SDK_INT < 17 && !activity.isFinishing()) {
            Dialog dialog;
            if (mTheme > 0) {
                dialog = new Dialog(activity, mTheme);
            } else {
                dialog = new Dialog(activity);
            }
            setupDialog(dialog);
            dialog.setCancelable(false);
            dialog.show();

            if (mSirenListener != null) {
                mSirenListener.onShowUpdateDialog();
            }
        }
    }

    private void setupDialog(final Dialog dialog) {
        dialog.setTitle(mSirenHelper.getLocalizedString(mActivityRef.get(), R.string.update_available, mLocale));
        dialog.setContentView(R.layout.siren_dialog);
        TextView message = (TextView) dialog.findViewById(R.id.tvSirenAlertMessage);
        Button update = (Button) dialog.findViewById(R.id.btnSirenUpdate);
        Button nextTime = (Button) dialog.findViewById(R.id.btnSirenNextTime);
        final Button skip = (Button) dialog.findViewById(R.id.btnSirenSkip);

        update.setText(mSirenHelper.getLocalizedString(mActivityRef.get(), R.string.update, mLocale));
        nextTime.setText(mSirenHelper.getLocalizedString(mActivityRef.get(), R.string.next_time, mLocale));
        skip.setText(mSirenHelper.getLocalizedString(mActivityRef.get(), R.string.skip_this_version, mLocale));

        message.setText(mSirenHelper.getAlertMessage(mActivityRef.get(), mMinAppVersion, mLocale));

        if (mSirenAlertType == SirenAlertType.FORCE
                || mSirenAlertType == SirenAlertType.OPTION
                || mSirenAlertType == SirenAlertType.SKIP) {
            update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSirenListener != null) {
                        mSirenListener.onLaunchGooglePlay();
                    }
                    dialog.dismiss();
                    mSirenHelper.openGooglePlay(mActivityRef.get());
                }
            });
        }

        if (mSirenAlertType == SirenAlertType.OPTION
                || mSirenAlertType == SirenAlertType.SKIP) {
            nextTime.setVisibility(View.VISIBLE);
            nextTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSirenListener != null) {
                        mSirenListener.onCancel();
                    }
                    dialog.dismiss();
                }
            });
        }
        if (mSirenAlertType == SirenAlertType.SKIP) {
            skip.setVisibility(View.VISIBLE);
            skip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSirenListener != null) {
                        mSirenListener.onSkipVersion();
                    }

                    mSirenHelper.setVersionSkippedByUser(mActivityRef.get(), mMinAppVersion);
                    dialog.dismiss();
                }
            });
        }
    }
}
