package com.example.administrator.quadrantswipe;

import android.inputmethodservice.InputMethodService;
import android.os.Vibrator;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.TextView;
import java.lang.Math;


public class QuadrantIME extends InputMethodService
{

    GestureDetector detector;
    public CharacterTree charMap;
    public CharacterTree numMap;
    public CharacterTree myCharTree;
    public boolean usingNums;
    private static final String TAG = "Swipetesting";
    private static final int SWIPE_MIN_DISTANCE = 25;
    private static final int SWIPE_THRESHOLD_VELOCITY = 20;
    private static boolean caps;
    int previewStringLength = 0;
    //Our keyboard's View
    View quadView;


    //Method gets called when the service starts. A View is 'inflated' for the user interface
    @Override
    public View onCreateInputView() {
        charMap = new CharacterTree(true);
        numMap = new CharacterTree(false);
        myCharTree = charMap;
        usingNums = false;
        caps = true;
        detector = new GestureDetector(this, new GestureListener());

        quadView = getLayoutInflater().inflate(R.layout.activity_quadrants, null);
        quadView.setBackgroundColor(0xCCb1e0f9);
        adjustView();
        Button myButton = (Button)quadView.findViewById(R.id.shift);
        myButton.setAlpha(.3f);


        //Attaches a listener to the custom view
        quadView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);
                return true;
            }
        });

        //updatePreview();
        return quadView;
    }


    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();

                if (diffX < 0 && diffY < 0) {
                    if (checkSwipe(diffX, diffY, velocityX, velocityY)) {
                        onSwipeUpLeft();
                        buzzMe();
                        return true;
                    }
                }
                if (diffX < 0 && diffY > 0) {
                    if (checkSwipe(diffX, diffY, velocityX, velocityY)) {
                        onSwipeDownLeft();
                        buzzMe();
                        return true;
                    }
                }
                if (diffX > 0 && diffY > 0) {
                    if (checkSwipe(diffX, diffY, velocityX, velocityY)) {
                        onSwipeDownRight();
                        buzzMe();
                        return true;
                    }
                }
                if (diffX > 0 && diffY < 0) {
                    if (checkSwipe(diffX, diffY, velocityX, velocityY)) {
                        onSwipeUpRight();
                        buzzMe();
                        return true;
                    }
                }

                result = true;
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }
    }





    public boolean checkSwipe(float diffX, float diffY, float velocityX, float velocityY) {
        return (Math.abs(diffX) > SWIPE_MIN_DISTANCE) && (Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
                && (Math.abs(diffY) > SWIPE_MIN_DISTANCE) && (Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY);
    }

    private void buzzMe(){
        Vibrator myBuzz = (Vibrator) getSystemService(this.VIBRATOR_SERVICE);
        myBuzz.vibrate(20);
    }

    public void onSwipeUpLeft() {
        Log.d(TAG, "Swipe Up-Left");
        handleText(myCharTree.onTopLeftSwipe());
    }
    public void onSwipeUpRight() {
        Log.d(TAG, "Swipe Up-Right");
        handleText(myCharTree.onTopRightSwipe());
    }

    public void onSwipeDownRight() {
        Log.d(TAG, "Swipe Down-Right");
        handleText(myCharTree.onBottomRightSwipe());
    }

    public void onSwipeDownLeft() {
        Log.d(TAG, "Swipe Down-Left");
        handleText(myCharTree.onBottomLeftSwipe());
    }



    public void onShiftClick(View view) {
        Button myButton = (Button)quadView.findViewById(R.id.shift);
        if (!caps) {
            caps = true;
            myButton.setAlpha(.3f);
        } else {
            caps = false;
            myButton.setAlpha(1f);
        }
        buzzMe();
        adjustView();
    }

    public void onNumClick(View view) {
        myCharTree.pointer = myCharTree.root;
        Button myButton = (Button)quadView.findViewById(R.id.numToggle);
        if(usingNums){
            myCharTree = charMap;
            usingNums = false;
            myButton.setText("123");
        }
        else
        {
            myCharTree = numMap;
            usingNums = true;
            myButton.setText("ABC");
        }
        buzzMe();
        adjustView();
    }

    public void onSpaceClick(View view) {
        InputConnection ic = getCurrentInputConnection();
        ExtractedTextRequest myReq = new ExtractedTextRequest();
        CharSequence charSequence =  ic.getExtractedText(myReq, 0).text;
        if (charSequence.length() > 0){
            if(charSequence.charAt(Math.max(charSequence.length() - 1, 0)) == ' '){
                ic.deleteSurroundingText(1, 0);
                handleText(". ");
                caps = true;
                adjustView();
                Button myButton = (Button)quadView.findViewById(R.id.shift);
                myButton.setAlpha(.3f);
            }
            else handleText(" ");
        }
        else
        {
            handleText(" ");
        }
        buzzMe();
    }

    public void onDelClick(View view) {
        if(myCharTree.pointer != myCharTree.root) {
            myCharTree.pointer = myCharTree.root;

        }
        else{
            Log.d(TAG, "Trying to Delete");
            InputConnection ic = getCurrentInputConnection();
            ic.deleteSurroundingText(1, 0);
            previewStringLength--;
            if (previewStringLength <= 0){
                caps = true;
                Button myButton = (Button)quadView.findViewById(R.id.shift);
                myButton.setAlpha(.3f);
                previewStringLength = 0;
            }
        }
        buzzMe();
        adjustView();
        updatePreview();
    }

    public void onEnterClick(View view) {
        InputConnection ic = getCurrentInputConnection();
        ic.performEditorAction(EditorInfo.IME_ACTION_DONE);
        ic.performEditorAction(EditorInfo.IME_ACTION_SEND);

        buzzMe();
    }


    public void handleText(String inText) {
        InputConnection ic = getCurrentInputConnection();

        if (inText != null) {
            if (caps) {
                inText = inText.toUpperCase();
            }
            ic.commitText(inText, 1);
            previewStringLength++;
            caps = false;
            Button myButton = (Button)quadView.findViewById(R.id.shift);
            myButton.setAlpha(1f);
        }
        updatePreview();
        adjustView();
    }

    private String padTextTL(String inText) {
        inText = inText+ "    \n     \n     ";
        return inText;
    }
    private String padTextTR(String inText) {
        inText = "    " +inText+ "\n     \n     ";
        return inText;
    }
    private String padTextBL(String inText) {
        inText = "     \n     \n" +inText+"    ";
        return inText;
    }
    private String padTextBR(String inText) {
        inText = "     \n     \n    " + inText;
        return inText;
    }

    public void updatePreview(){
        InputConnection ic = getCurrentInputConnection();
        ExtractedTextRequest myReq = new ExtractedTextRequest();
        TextView outputText = (TextView) quadView.findViewById(R.id.outputText);
        outputText.setText(ic.getExtractedText(myReq, 0).text);
        //Log.d(TAG, "Extracted text is: " + ic.getExtractedText(myReq, 0).text.toString());
    }


    public void adjustView()
    {

        if(myCharTree.pointer == myCharTree.root || myCharTree.pointer == myCharTree.root.bottomRight){
            setSubMenu();
        }

        else if(!myCharTree.pointer.leaf) {
            String myString = myCharTree.pointer.topRight.data;
            setTopRight(padTextTR(myString));
            myString = myCharTree.pointer.bottomRight.data;
            setBottomRight(padTextBR(myString));
            myString = myCharTree.pointer.bottomLeft.data;
            setBottomLeft(padTextBL(myString));
            myString = myCharTree.pointer.topLeft.data;
            setTopLeft(padTextTL(myString));
        }
    }


    public void setSubMenu(){
        String myString = myCharTree.pointer.topLeft.topLeft.data + "   "
                + myCharTree.pointer.topLeft.topRight.data + "\n" + "     " + "\n"
                + myCharTree.pointer.topLeft.bottomLeft.data + "   "
                + myCharTree.pointer.topLeft.bottomRight.data;
        setTopLeft(myString);
        myString = myCharTree.pointer.topRight.topLeft.data + "   "
                + myCharTree.pointer.topRight.topRight.data + "\n" + "     " + "\n"
                + myCharTree.pointer.topRight.bottomLeft.data + "   "
                + myCharTree.pointer.topRight.bottomRight.data;
        setTopRight(myString);

        myString = myCharTree.pointer.bottomLeft.topLeft.data + "   "
                + myCharTree.pointer.bottomLeft.topRight.data + "\n" + "     " + "\n"
                + myCharTree.pointer.bottomLeft.bottomLeft.data + "   "
                + myCharTree.pointer.bottomLeft.bottomRight.data;
        setBottomLeft(myString);
        if(myCharTree.pointer != myCharTree.root || usingNums){
        myString = myCharTree.pointer.bottomRight.topLeft.data + "   "
                + myCharTree.pointer.bottomRight.topRight.data + "\n" + "    " + "\n"
                + myCharTree.pointer.bottomRight.bottomLeft.data + "   "
                + myCharTree.pointer.bottomRight.bottomRight.data;
        setBottomRight(myString);}
        else{
            myString = "     \n     \n More";      //getString(R.string.menu_br);
            setBottomRight(myString);
        }
    }


    public void setTopLeft(String s){
        TextView newView = (TextView) quadView.findViewById(R.id.topLeft);
        if(caps){
            newView.setText(s.toUpperCase());

        }
        else{
            newView.setText(s);
        }
    }

    public void setTopRight(String s){
        TextView newView = (TextView) quadView.findViewById(R.id.topRight);
        if(caps){
            newView.setText(s.toUpperCase());
        }
        else{
            newView.setText(s);
        }

    }
    public void setBottomLeft(String s){
        TextView newView = (TextView) quadView.findViewById(R.id.botLeft);
        if(caps){
            newView.setText(s.toUpperCase());
        }
        else{
            newView.setText(s);
        }
    }
    public void setBottomRight(String s){
        TextView newView = (TextView) quadView.findViewById(R.id.botRight);
        if(caps){
            newView.setText(s.toUpperCase());
        }
        else{
            newView.setText(s);
        }
    }
    }



