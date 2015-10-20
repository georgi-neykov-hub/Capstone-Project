package com.neykov.podcastportal.view.base;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class AlertDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {

    private static final String ARG_DIALOG_PARAMS = "AlertDialogFragment.ARG_DIALOG_PARAMS";

    @IntDef({DialogInterface.BUTTON_NEGATIVE, DialogInterface.BUTTON_NEUTRAL, DialogInterface.BUTTON_POSITIVE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DialogButton {
    }

    public interface OnDialogFragmentClickListener {

        void onClick(DialogFragment dialog, @DialogButton int which, int dialogId);
    }

    private static class AlertParams implements Parcelable {
        private int dialogId = 0;
        private
        @StyleRes
        int mTheme;
        private
        @StyleRes
        int mDialogTheme;
        private CharSequence mTitle;
        private CharSequence mMessage;
        private CharSequence mPositiveButtonText;
        private CharSequence mNegativeButtonText;
        private CharSequence mNeutralButtonText;

        private boolean mCancelable = true;

        private AlertParams(@StyleRes int theme, @StyleRes int dialogTheme) {
            this.mTheme = theme;
            this.mDialogTheme = dialogTheme;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.dialogId);
            dest.writeInt(this.mTheme);
            dest.writeInt(this.mDialogTheme);
            dest.writeString(mTitle != null ? this.mTitle.toString() : null);
            dest.writeString(mMessage != null ? mMessage.toString() : null);
            dest.writeString(mNegativeButtonText != null ? mPositiveButtonText.toString() : null);
            dest.writeString(mNeutralButtonText != null ? mNeutralButtonText.toString() : null);
            dest.writeByte(mCancelable ? (byte) 1 : (byte) 0);
        }

        protected AlertParams(Parcel in) {
            this.dialogId = in.readInt();
            this.mTheme = in.readInt();
            this.mTitle = in.readString();
            this.mDialogTheme = in.readInt();
            this.mMessage = in.readString();
            this.mPositiveButtonText = in.readString();
            this.mNegativeButtonText = in.readString();
            this.mNeutralButtonText = in.readString();
            this.mCancelable = in.readByte() != 0;
        }

        public static final Parcelable.Creator<AlertParams> CREATOR = new Parcelable.Creator<AlertParams>() {
            public AlertParams createFromParcel(Parcel source) {
                return new AlertParams(source);
            }

            public AlertParams[] newArray(int size) {
                return new AlertParams[size];
            }
        };

    }

    @SuppressWarnings("unused")
    public static class Builder {
        private Context mContext;

        private AlertParams mParams;

        /**
         * Same as {@link #Builder(Context, int, int)} with 0 set to both theme arguments
         */
        public Builder(Context context) {
            this(context, 0, 0);
        }

        /**
         * Constructor using a context and theme names for this builder and
         * the {@link AlertDialogFragment} it creates.
         * to get the dialog's style (such as {@link android.R.attr#alertDialogTheme}.
         *
         * @param theme       the theme to be used when inflating the fragment's view
         * @param dialogTheme the dialog theme to be used, as per
         *                    {@link android.support.v7.app.AlertDialog.Builder#Builder(Context, int)}
         */
        public Builder(Context context, @StyleRes int theme, @StyleRes int dialogTheme) {
            mContext = context;
            mParams = new AlertParams(theme, dialogTheme);
        }

        /**
         * Set the ID of the #AlertDialogFragment being built.
         * This value will be returned in
         * {@link OnDialogFragmentClickListener#onClick(DialogFragment, int, int)}
         *
         * @param dialogId the dialog id
         * @return @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setId(int dialogId) {
            mParams.dialogId = dialogId;
            return this;
        }

        /**
         * Set the title using the given resource id.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setTitle(@StringRes int titleId) {
            return setTitle(mContext.getString(titleId));
        }

        /**
         * Set the title displayed in the {@link Dialog}.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setTitle(CharSequence title) {
            mParams.mTitle = title;
            return this;
        }

        /**
         * Set the message to display using the given resource id.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setMessage(@StringRes int messageId) {
            return setMessage(mContext.getString(messageId));
        }

        /**
         * Set the message to display using the given resource id and format arguments.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setMessage(@StringRes int messageId, Object... args) {
            return setMessage(mContext.getString(messageId, args));
        }

        /**
         * Set the message to display.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setMessage(CharSequence mMessage) {
            mParams.mMessage = mMessage;
            return this;
        }

        /**
         * Set the message to display as the positive button of the dialog.
         *
         * @param textId The resource id of the text to display in the positive button
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setPositiveButton(@StringRes int textId) {
            return setPositiveButton(mContext.getString(textId));
        }

        /**
         * Set the message to display as the negative button of the dialog.
         *
         * @param textId The resource id of the text to display in the negative button
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setNegativeButton(@StringRes int textId) {
            return setNegativeButton(mContext.getString(textId));
        }

        /**
         * Set the message to display as the neutrale button of the dialog.
         *
         * @param textId The resource id of the text to display in the neutral button
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setNeutralButton(@StringRes int textId) {
            return setNeutralButton(mContext.getString(textId));
        }

        /**
         * Set the message to display as the positive button of the dialog.
         *
         * @param text The text to display in the positive button
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setPositiveButton(CharSequence text) {
            mParams.mPositiveButtonText = text;
            return this;
        }

        /**
         * Set the message to display as the negative button of the dialog.
         *
         * @param text The text to display in the negative button
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setNegativeButton(CharSequence text) {
            mParams.mNegativeButtonText = text;
            return this;
        }

        /**
         * Set the message to display as the neutral button of the dialog.
         *
         * @param text The text to display in the neutral button
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setNeutralButton(CharSequence text) {
            mParams.mNeutralButtonText = text;
            return this;
        }

        /**
         * Sets whether the dialog is cancelable or not.  Default is true.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setCancelable(boolean cancelable) {
            mParams.mCancelable = cancelable;
            return this;
        }

        /**
         * Creates a {@link AlertDialogFragment} with the arguments supplied to this builder. It does not
         * {@link DialogFragment#show(FragmentManager, String)} the fragment.
         * Use {@link #show(FragmentManager, String)} or {@link #show(FragmentTransaction, String)}
         * if you don't have any other processing
         * to do and want this to be created and displayed.
         */
        public AlertDialogFragment create() {
            Bundle args = new Bundle();
            args.putParcelable(ARG_DIALOG_PARAMS, mParams);
            AlertDialogFragment instance = new AlertDialogFragment();
            instance.setArguments(args);
            return instance;
        }

        /**
         * Creates a new instance and displays the dialog, adding the fragment to the given FragmentManager.
         * For more details see {@link DialogFragment#show(FragmentManager, String)}
         *
         * @param manager The FragmentManager this fragment will be added to.
         * @param tag     The tag for this fragment, as per
         *                {@link FragmentTransaction#add(Fragment, String) FragmentTransaction.add}.
         */
        public void show(FragmentManager manager, String tag) {
            create().show(manager, tag);
        }

        /**
         * Creates a new instance and displays the dialog, adding the fragment using an existing transaction
         * and then committing the transaction.
         *
         * @param transaction An existing transaction in which to add the fragment.
         * @param tag         The tag for this fragment, as per
         *                    {@link FragmentTransaction#add(Fragment, String) FragmentTransaction.add}.
         * @return Returns the identifier of the committed transaction, as per
         * {@link FragmentTransaction#commit() FragmentTransaction.commit()}.
         */
        public int show(FragmentTransaction transaction, String tag) {
            return create().show(transaction, tag);
        }
    }


    private AlertParams mParams;
    private OnDialogFragmentClickListener mListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mParams = getArguments().getParcelable(ARG_DIALOG_PARAMS);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Object parent = getHost();
        if (parent instanceof OnDialogFragmentClickListener) {
            mListener = (OnDialogFragmentClickListener) parent;
        }
    }

    @Override
    public void onDetach() {
        mListener = null;
        super.onDetach();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity(), mParams.mDialogTheme)
                .setTitle(mParams.mTitle)
                .setMessage(mParams.mMessage)
                .setPositiveButton(mParams.mPositiveButtonText, this)
                .setNegativeButton(mParams.mNegativeButtonText, this)
                .setNeutralButton(mParams.mNeutralButtonText, this)
                .setCancelable(mParams.mCancelable)
                .create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (mListener != null) {
            mListener.onClick(this, which, mParams.dialogId);
        }
    }
}
