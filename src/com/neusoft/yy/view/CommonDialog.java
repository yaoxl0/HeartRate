package com.neusoft.yy.view;

import com.neusoft.yy.heart_rate_monitor.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author YXL
 *
 */
public class CommonDialog extends Dialog {

	/**
	 * @param context
	 */
	public CommonDialog(Context context) {
		super(context);
	}

	public CommonDialog(Context context, int theme) {
		super(context, theme);
	}
	
	public static class Builder {

		private Context mContext;
		private int mIcon = -1;// ��ʾͼ��
		private CharSequence mTitle;// ��ʾ����
		private CharSequence mMessage;// ��ʾ����
		private CharSequence mPositiveButtonText;// ȷ����ť�ı�
		private CharSequence mNegativeButtonText;// �ܾ���ť�ı�
		private CharSequence mNeutralButtonText;// �м䰴ť�ı�
		private boolean mCancelable = true;// �Ƿ�����ȡ����
		private int mViewSpacingLeft;
		private int mViewSpacingTop;
		private int mViewSpacingRight;
		private int mViewSpacingBottom;
		private boolean mViewSpacingSpecified = false;
		private boolean mCanceledOnTouchOutsideAble = true;
		
		// ��ʾ����View
		private View mView;

		private OnClickListener mPositiveButtonClickListener,
				mNegativeButtonClickListener, mNeutralButtonClickListener;
		private OnCancelListener mCancelListener;// ȡ�����¼�
		private OnKeyListener mKeyListener;// ��������

		public Builder(Context context) {
			this.mContext = context;
		}

		public Builder setMessage(CharSequence message) {
			this.mMessage = message;
			return this;
		}

		public Builder setMessage(int message) {
			this.mMessage = mContext.getText(message);
			return this;
		}

		public Builder setTitle(int title) {
			this.mTitle = mContext.getText(title);
			return this;
		}

		public Builder setTitle(CharSequence title) {
			this.mTitle = title;
			return this;
		}

		public Builder setIcon(int icon) {
			this.mIcon = icon;
			return this;
		}

		public Builder setView(View view) {
			this.mView = view;
			mViewSpacingSpecified = false;
			return this;
		}

		public Builder setView(View view, int left, int top, int right,
				int bottom) {
			this.mView = view;
			this.mViewSpacingLeft = left;
			this.mViewSpacingTop = top;
			this.mViewSpacingRight = right;
			this.mViewSpacingBottom = bottom;
			mViewSpacingSpecified = true;
			return this;
		}

		public Builder setPositiveButton(int textId,
				final OnClickListener listener) {
			this.mPositiveButtonText = mContext.getText(textId);
			this.mPositiveButtonClickListener = listener;
			return this;
		}

		public Builder setPositiveButton(String text,
				final OnClickListener listener) {
			this.mPositiveButtonText = text;
			this.mPositiveButtonClickListener = listener;
			return this;
		}

		public Builder setNeutralButton(int textId,
				final OnClickListener listener) {
			this.mNeutralButtonText = mContext.getText(textId);
			this.mNeutralButtonClickListener = listener;
			return this;
		}

		public Builder setNeutralButton(String text,
				final OnClickListener listener) {
			this.mNeutralButtonText = text;
			this.mNeutralButtonClickListener = listener;
			return this;
		}

		public Builder setNegativeButton(int textId,
				final OnClickListener listener) {
			this.mNegativeButtonText = mContext.getText(textId);
			this.mNegativeButtonClickListener = listener;
			return this;
		}

		public Builder setNegativeButton(String text,
				final OnClickListener listener) {
			this.mNegativeButtonText = text;
			this.mNegativeButtonClickListener = listener;
			return this;
		}

		public Builder setCancelable(boolean cancelable) {
			this.mCancelable = cancelable;
			return this;
		}

		public Builder setOnCancelListener(OnCancelListener listener) {
			this.mCancelListener = listener;
			return this;
		}

		public Builder setOnKeyListener(OnKeyListener listener) {
			this.mKeyListener = listener;
			return this;
		}
		
		public Builder setsCanceledOnTouchOutside(boolean canceledOnTouchOutsideAble) {
			this.mCanceledOnTouchOutsideAble = canceledOnTouchOutsideAble;
			return this;
		}
		
		public CommonDialog create() {
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			final CommonDialog dialog = new CommonDialog(mContext, R.style.commondialogstyle);
			dialog.setCancelable(mCancelable);
			// ����ȡ�����¼�
			if (mCancelListener != null) {
				dialog.setOnCancelListener(mCancelListener);
			}
			dialog.setCanceledOnTouchOutside(mCanceledOnTouchOutsideAble);
			// ���ü��̼����¼�
			if (mKeyListener != null) {
				dialog.setOnKeyListener(mKeyListener);
			}
			// ��ȡ�Ի��򲼾�
			View layout = inflater.inflate(R.layout.common_dialog,
					(ViewGroup) (((Activity) mContext)
							.findViewById(R.id.parentPanel)));
			layout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT));
			// ���ñ���
			((TextView) layout.findViewById(R.id.alertTitle)).setText(mTitle);
			// ����ͼ��
			if (mIcon != -1) {
				((ImageView) layout.findViewById(R.id.icon))
						.setBackgroundResource(mIcon);
			} else {
				layout.findViewById(R.id.icon).setVisibility(View.GONE);
			}
			int count = 0;
			// ���þܾ���ť
			if(setButton(layout, mNegativeButtonText, R.id.button1, dialog, mNegativeButtonClickListener)) count++;
			// ����ȷ����ť
			if(setButton(layout, mPositiveButtonText, R.id.button2, dialog, mPositiveButtonClickListener)) count++;
			// �����м䰴ť
			if(setButton(layout, mNeutralButtonText, R.id.button3, dialog, mNeutralButtonClickListener)) count++;
			
			if(count==0){
				layout.findViewById(R.id.buttonPanel).setVisibility(View.GONE);
			}
			//һ����ťʱ����ʾ���߿ռ�
			if (count == 1) {
				layout.findViewById(R.id.leftSpacer)
						.setVisibility(View.INVISIBLE);
				layout.findViewById(R.id.rightSpacer).setVisibility(
						View.INVISIBLE);
			}
			// ������ʾ��Ϣ
			if (!TextUtils.isEmpty(mMessage)) {
				((TextView) layout.findViewById(R.id.message))
						.setText(mMessage);
			} else {
				((LinearLayout) layout.findViewById(R.id.contentPanel))
						.setVisibility(View.GONE);
			}
			// ������ʾ���ݲ���
			if (mView != null) {
				final FrameLayout customPanel = (FrameLayout) layout
						.findViewById(R.id.customPanel);
				if (mViewSpacingSpecified) {
					customPanel.setPadding(mViewSpacingLeft, mViewSpacingTop,
							mViewSpacingRight, mViewSpacingBottom);
				}
				customPanel.addView(mView);
			} else {
				((FrameLayout) layout.findViewById(R.id.customPanel))
						.setVisibility(View.GONE);
			}
			dialog.setContentView(layout);
			return dialog;
		}
	
		public CommonDialog show() {
			CommonDialog dialog = create();
			dialog.show();
			return dialog;
		}

		private boolean setButton(View layout, CharSequence mPositiveButtonText2, int id,
				final Dialog dialog, final OnClickListener listener) {
			if (!TextUtils.isEmpty(mPositiveButtonText2)) {
				final Button button1 = (Button) layout.findViewById(id);
				button1.setText(mPositiveButtonText2);
				if (listener != null) {
					button1.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							listener.onClick(dialog,
									DialogInterface.BUTTON_POSITIVE);
						}
					});
				}else{
					//Ĭ���¼�Ϊ�رնԻ���
					button1.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							dialog.cancel();
							dialog.dismiss();
						}
					});
				}
				return true;
			} else {
				layout.findViewById(id).setVisibility(View.GONE);
				return false;
			}
		}
	}	
		
	
}
