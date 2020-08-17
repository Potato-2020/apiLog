package com.potato.tools;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.potato.apilogvisual.R;


/**
 * 弹出输入
 */

public class InputPopWindow extends PopupWindow implements View.OnClickListener {
    private Activity mActivity;
    private PopupWindow mPop;
    private WindowManager.LayoutParams params;
    private View rootView;
    private EditText et_text;
    private Object flag;

    // mHint输入框提示内容  leftBtnStr左边按钮 rightbtnStr右边按钮
    public InputPopWindow(final Activity mActivity, String mHint, String leftBtnStr, String rightbtnStr) {
        super(mActivity);
        this.mActivity = mActivity;
        if (mPop == null) {
            params = mActivity.getWindow().getAttributes();
            rootView = View.inflate(mActivity, R.layout.pop_input, null);
            rootView.setFocusable(true); // 这个很重要
            rootView.setFocusableInTouchMode(true);
            mPop = new PopupWindow(rootView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            mPop.setFocusable(true);
            mPop.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            TextView tvNo = rootView.findViewById(R.id.tv_no);
            TextView tvYes = rootView.findViewById(R.id.tv_yes);
            tvNo.setText(leftBtnStr);
            tvYes.setText(rightbtnStr);
            et_text = rootView.findViewById(R.id.et_text);
            et_text.setHint(mHint);
            tvNo.setOnClickListener(this);
            tvYes.setText(rightbtnStr);
            tvYes.setOnClickListener(this);
            rootView.findViewById(R.id.touch_outside).setOnClickListener(this);
            // mPop.setAnimationStyle(R.style.AnimBottomIn);
            //当你发现有背景色时，需给布局文件设置背景色，这样即可覆盖系统自带的背景色。
            mPop.setBackgroundDrawable(new BitmapDrawable());
            mPop.setOutsideTouchable(true);
            mPop.setFocusable(true);
            //http://blog.csdn.net/misly_vinky/article/details/11210187/
            //http://blog.csdn.net/chenguang79/article/details/43016519
            //android中popupwindow弹出后，屏幕背景变成半透明

            KeyboardUtils.showKeyBoard(et_text, mActivity);
            mPop.setOnDismissListener(new OnDismissListener() {
                @Override
                public void onDismiss() {
                    backgroundAlpha(1);
                }
            });
            KeyBoardListener.setListener(mActivity, new KeyBoardListener.OnSoftKeyBoardChangeListener() {
                @Override
                public void keyBoardShow(int height) {

                }

                @Override
                public void keyBoardHide(int height) {
                    if (mPop != null) {
                        mPop.dismiss();
                    }
                }
            });
        }
    }

    public void setInputType(int inputType) {
        try {
            et_text.setInputType(inputType);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setMaxLength(int length) {
        et_text.setFilters(new InputFilter[]{new InputFilter.LengthFilter(length)});
    }


    public void show(String text, String hint, Object flag) {
        if (mPop != null && !mPop.isShowing()) {
            mPop.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    backgroundAlpha(0.5f);
                }
            }, 100);
        }
        if (et_text != null && text != null) {
            et_text.setText(text);
            et_text.setSelection(text.length());
        }
        this.flag = flag;
    }


    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        if (bgAlpha == 1) {
            mActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//不移除该Flag的话,在有视频的页面上的视频会出现黑屏的bug
        } else {
            mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//此行代码主要是解决在华为手机上半透明效果无效的bug
        }
        mActivity.getWindow().setAttributes(lp);
    }


    @Override
    public void onClick(View v) {
        params.alpha = 1f;
        mActivity.getWindow().setAttributes(params);
        int id = v.getId();
        if (id == R.id.tv_yes) {
            if (mItemsOnClick != null) {
                mItemsOnClick.onRightClick(et_text.getText().toString(), this.flag);
            }
            mPop.dismiss();
        } else if (id == R.id.tv_no) {
            if (mItemsOnClick != null) {
                mItemsOnClick.onLeftClick(et_text.getText().toString(), this.flag);
            }
            mPop.dismiss();
        } else if (id == R.id.touch_outside) {//外部
            mPop.dismiss();
        }
    }


    public interface ItemsOnClick {
        void onLeftClick(String mInput, Object flag);

        void onRightClick(String mInput, Object flag);
    }

    private ItemsOnClick mItemsOnClick;

    public void setmItemsOnClick(ItemsOnClick mItemsOnClick) {
        this.mItemsOnClick = mItemsOnClick;
    }


    public void maxBytes(int max) {
        et_text.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int count, int after) {
                int mTextMaxLength = 0;
                Editable editable = et_text.getText();
                String str = editable.toString().trim();
                //得到最初字段的长度大小，用于光标位置的判断
                int selEndIndex = Selection.getSelectionEnd(editable);
                // 取出每个字符进行判断，如果是字母数字和标点符号则为一个字符加1，
                // 如果是汉字则为两个字符
                for (int i = 0; i < str.length(); i++) {
                    char charAt = str.charAt(i);
                    //32-122包含了空格，大小写字母，数字和一些常用的符号，
                    //如果在这个范围内则算一个字符，
                    //如果不在这个范围比如是汉字的话就是两个字符
                    if (charAt >= 32 && charAt <= 122) {
                        mTextMaxLength++;
                    } else {
                        mTextMaxLength += 2;
                    }
                    // 当最大字符大于40时，进行字段的截取，并进行提示字段的大小
                    if (mTextMaxLength > max) {
                        // 截取最大的字段
                        String newStr = str.substring(0, i);
                        et_text.setText(newStr);
                        // 得到新字段的长度值
                        editable = et_text.getText();
                        int newLen = editable.length();
                        if (selEndIndex > newLen) {
                            selEndIndex = editable.length();
                        }
                        // 设置新光标所在的位置
                        Selection.setSelection(editable, selEndIndex);
                        break;
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }


}
