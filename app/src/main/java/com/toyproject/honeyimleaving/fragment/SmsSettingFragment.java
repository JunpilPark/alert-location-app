package com.toyproject.honeyimleaving.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.app.Activity;
import android.widget.Toast;

import com.toyproject.honeyimleaving.R;
import com.toyproject.honeyimleaving.myutil.Dlog;
import com.toyproject.honeyimleaving.myutil.Util;

import java.util.ArrayList;

/**
 * Created by iamfe on 2018-09-12.
 */

public class SmsSettingFragment extends Fragment implements FragmentReturnInterface<ArrayList> {

    private EditText mEditSmsReceiver;
    private EditText mEditSmsContents;

    private String mSmsContents;
    private ArrayList<String> mSmsReceiver;
    private boolean mIsError;
    private String mErrMessage;
    private CheckBox mChkAlertMe;
    private boolean mIsAlertMe;

    private ImageButton mBtnSearchContact;
    private final int REQUEST_CONTACTS = 8000;

    public SmsSettingFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Dlog.d("프레그먼트의 onCreate 호출");
        super.onCreate(savedInstanceState);
        mIsError = false;

        mSmsContents = null;
        mSmsReceiver = null;
        if (getArguments() != null) {
            mSmsContents =  getArguments().getString("smsContents");
            mSmsReceiver = (ArrayList<String>) getArguments().getSerializable("mobileNumbers");
            mIsAlertMe = getArguments().getBoolean("alertMe", false);

            Dlog.d("프레그먼트의 onCreate 호출 - 파라메터있음");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.frag_sms_setting, container, false);
        mEditSmsContents = layout.findViewById(R.id.edit_sms_contents);
        mEditSmsReceiver = layout.findViewById(R.id.edit_sms_receiver);
        mChkAlertMe = layout.findViewById(R.id.chk_alert_me);
        mBtnSearchContact = layout.findViewById(R.id.btn_search_contact);

        setSmsInfoToEditView();
        setAlerMeCheckBox();

        mBtnSearchContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                //intent.setData(ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(intent, REQUEST_CONTACTS);
            }
        });
        return layout;
    }

    @Override
    public ArrayList getFragementReturn() {
        try {
         //   mSmsContents = String.valueOf(mEditSmsContents.getText());
         //   mSmsReceiver = Util.getMobileArrList(String.valueOf(mEditSmsReceiver.getText()));
           return makeReturnArrList();

        }
        catch (Exception e) {
            Dlog.d(e.getMessage());
            mErrMessage = e.getMessage();
            mIsError = true;
            return null;
        }
    }

    @Override
    public String getErrorString() {
        return mErrMessage;
    }

    @Override
    public boolean isError() {
        return checkValidation();
    }


    private boolean checkValidation() {
        mIsError = false;

        mSmsContents = String.valueOf(mEditSmsContents.getText());
        mSmsReceiver = Util.getMobileArrList(String.valueOf(mEditSmsReceiver.getText()));

        if(mChkAlertMe.isChecked() == false && ((mSmsReceiver == null) || (mSmsReceiver.size() <= 0))) {
            mIsError = true;
            mErrMessage = getString(R.string.txt_sms_validation_check1);
            return mIsError;
        }

        if(mSmsReceiver != null && mSmsReceiver.size() > 0) {
            // 전화번호가 숫자 이외의 문자가 있는지 확인
            for(int i = 0 ; i < mSmsReceiver.size() ; i++) {
                if(Util.isNumeric(mSmsReceiver.get(i)) == false) {
                    mIsError = true;
                    mErrMessage = getString(R.string.txt_sms_validation_check3);
                    return mIsError;
                }
            }
            // 문자 내용이 없을 때
            if(mSmsContents.length() <=0) {
                mIsError = true;
                mErrMessage = getString(R.string.txt_sms_validation_check2);
                return mIsError;
            }
        }

        return mIsError;
    }

    private void setSmsInfoToEditView() {
        if(mEditSmsReceiver == null || mEditSmsContents == null) return;

        if(mSmsContents != null) {
            mEditSmsContents.setText(mSmsContents);
        }

        if(mSmsReceiver != null) {
            mEditSmsReceiver.setText(Util.getMobileString(mSmsReceiver));
        }
    }

    private void setAlerMeCheckBox() {
        if(mIsAlertMe == true) {
            mChkAlertMe.setChecked(true);
        }
        else {
            mChkAlertMe.setChecked(false);
        }
    }

    /**
     * 플래그먼트에서 리턴 할 때는 0번째 index에는 나에게 알림을 알릴지, 1번 index 에는 conetents를 2번부터 연락처를 실어서 리턴함.
     * @return ArrayList<String> 0번 인덱스는 나에게 알림여부 , 1번 인덱스는 contents 2번 인덱스부터 연락처를 리턴
     */
    private ArrayList<String> makeReturnArrList() {
        ArrayList<String> retrunArrList;
        if(mSmsReceiver == null) {
            retrunArrList = new ArrayList<>();
        }
        else {
            retrunArrList =(ArrayList<String>) mSmsReceiver.clone();
        }
        if(mSmsContents == null) {
            mSmsContents = "";
        }

        retrunArrList.add(0, mSmsContents);

        if(mChkAlertMe.isChecked() == true) {
            retrunArrList.add(0, "Y");
        }
        else {
            retrunArrList.add(0, "N");
        }

        return retrunArrList;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
            Toast.makeText(getContext(), R.string.txt_load_fail_contact, Toast.LENGTH_SHORT).show();
            return;
        }
        if (requestCode == REQUEST_CONTACTS) {
            Uri contactData = data.getData();
            if(contactData == null) return;
            Cursor cursor = getContext().getContentResolver().query(contactData,null, null, null, null);

            StringBuffer tempReceiverNumberInCur = new StringBuffer();
            if(cursor != null && cursor.moveToFirst()) {
                for (int i = 0; i < cursor.getCount(); i++) {
                    if (i != 0) {
                        tempReceiverNumberInCur.append(",");
                    }
                    tempReceiverNumberInCur.append(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replace("-",""));
                    cursor.moveToNext();
                }
                pasteNumberToSmsReceiverEdit(tempReceiverNumberInCur.toString());
           }
        }
    }

    private void pasteNumberToSmsReceiverEdit(String moblieNumber) {
        if(mEditSmsReceiver == null) return;
        String preString = mEditSmsReceiver.getText().toString();
        String newString;
        if(preString.length() > 0) {
            newString = preString +"," + moblieNumber;
        }
        else {
            newString = moblieNumber;
        }
        mEditSmsReceiver.setText(newString);
        mEditSmsReceiver.setSelection(newString.length());
    }
}
