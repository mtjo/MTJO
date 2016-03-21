package com.wanyou.aframe.ui.widget.emoji;

import java.lang.ref.SoftReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.wanyou.wylibrary.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.widget.TextView;

public class EmojiTextView extends TextView {
	private int mEmojiconSize;
    private int mEmojiconTextSize;
    private int mTextStart = 0;
    private int mTextLength = -1;
    private boolean mUseSystemDefault = false;

    public EmojiTextView(Context context) {
        super(context);
        init(null);
    }

    public EmojiTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public EmojiTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        mEmojiconTextSize = (int) getTextSize();
        if (attrs == null) {
            mEmojiconSize = (int) getTextSize();
        } else {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.Emojicon);
            mEmojiconSize = (int) a.getDimension(R.styleable.Emojicon_emojiconSize, getTextSize());
            mTextStart = a.getInteger(R.styleable.Emojicon_emojiconTextStart, 0);
            mTextLength = a.getInteger(R.styleable.Emojicon_emojiconTextLength, -1);
            mUseSystemDefault = a.getBoolean(R.styleable.Emojicon_emojiconUseSystemDefault, false);
            a.recycle();
        }
        setText(getText());
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (!TextUtils.isEmpty(text)) {
        	//解析qq表情
        	text = text.toString().replaceAll("&amp;", "&").replaceAll("&lt;", "<").replaceAll("&gt;", ">");
        	SpannableStringBuilder builder = new SpannableStringBuilder(text);
        	replace(builder);
        	
            EmojiconHandler.addEmojis(getContext(), builder, mEmojiconSize, mEmojiconTextSize, mTextStart, mTextLength, mUseSystemDefault);
            text = builder;
        }
        super.setText(text, type);
    }

    /**
     * Set the size of emojicon in pixels.
     */
    public void setEmojiconSize(int pixels) {
        mEmojiconSize = pixels;
        super.setText(getText());
    }

    /**
     * Set whether to use system default emojicon
     */
    public void setUseSystemDefault(boolean useSystemDefault) {
        mUseSystemDefault = useSystemDefault;
    }
    
    private float scale = 0.5f;
    private String qqfaceRegex = "/::\\)|/::~|/::B|/::\\||/:8-\\)|/::<|/::$|/::X|/::Z|/::'\\(|/::-\\||/::@|/::P|/::D|/::O|/::\\(|/::\\+|/:--b|/::Q|/::T|/:,@P|/:,@-D|/::d|/:,@o|/::g|/:\\|-\\)|/::!|/::L|/::>|/::,@|/:,@f|/::-S|/:\\?|/:,@x|/:,@@|/::8|/:,@!|/:!!!|/:xx|/:bye|/:wipe|/:dig|/:handclap|/:&-\\(|/:B-\\)|/:<@|/:@>|/::-O|/:>-\\||/:P-\\(|/::'\\||/:X-\\)|/::\\*|/:@x|/:8\\*|/:pd|/:<W>|/:beer|/:basketb|/:oo|/:coffee|/:eat|/:pig|/:rose|/:fade|/:showlove|/:heart|/:break|/:cake|/:li|/:bome|/:kn|/:footb|/:ladybug|/:shit|/:moon|/:sun|/:gift|/:hug|/:strong|/:weak|/:share|/:v|/:@\\)|/:jj|/:@@|/:bad|/:lvu|/:no|/:ok|/:love|/:<L>|/:jump|/:shake|/:<O>|/:circle|/:kotow|/:turn|/:skip|/:oY|/:#-0|/:hiphot|/:kiss|/:<&|/:&>";
	private void replace(Spannable text) {
		try {
            Pattern pattern = Pattern.compile(qqfaceRegex);
            Matcher matcher = pattern.matcher(text);
            boolean result = matcher.find();
//            if(!matcher.find()) {
//                return ;
//            }
            while(result){
            	if(WXFaceUtils.mEmoticons_CN_Id.containsKey(matcher.group())) {
                    Bitmap bitmap = getBitmap(matcher.group());
                    if(bitmap != null) {
                        Matrix matrix = new Matrix();
//                        matrix.postScale(scale, scale);
                        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                        ImageSpan span = new ImageSpan(getContext(), resizeBmp);
                        text.setSpan(span, matcher.start(), matcher.end(), 0x21);
                    }
                }
                if(WXFaceUtils.mEmoticonsId.containsKey(matcher.group())) {
                	Bitmap bitmap = getBitmap(matcher.group());
                    if(bitmap != null) {
                        Matrix matrix = new Matrix();
//                        matrix.postScale(scale, scale);
                        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                        ImageSpan span = new ImageSpan(getContext(), resizeBmp);
                        text.setSpan(span, matcher.start(), matcher.end(), 0x21);
                    }
                }
                
                result = matcher.find();
            }
            
        }catch(Exception e) {
        	
        }
    }
	
	private Bitmap getBitmap(String key) {
        Bitmap bitmap = WXFaceUtils.mEmoticonsChche.get(key).get();
        if(bitmap == null) {
            if(WXFaceUtils.mEmoticons_CN_Id.containsKey(key)) {
                int id = (Integer)WXFaceUtils.mEmoticons_CN_Id.get(key).intValue();
                bitmap = BitmapFactory.decodeResource(getContext().getResources(), id);
                WXFaceUtils.mEmoticonsChche.put(key, new SoftReference(bitmap));
                return bitmap;
            }
            if(WXFaceUtils.mEmoticonsId.containsKey(key)) {
                int id = (Integer)WXFaceUtils.mEmoticonsId.get(key).intValue();
                bitmap = BitmapFactory.decodeResource(getContext().getResources(), id);
                WXFaceUtils.mEmoticonsChche.put(key, new SoftReference(bitmap));
            }
        }
        return bitmap;
    }
}