package com.example.administrator.quadrantswipe;

import android.view.View;


public class CharacterTree {

    TreeNode root;
    TreeNode pointer;
    StringBuffer outputBuffer;
    boolean isChars;
    private String[] charMap = new String[]{
        "i", "a", "l", "h", "o", "n", "r", "s", "m", "u", "e", "t",
                "q", "z", "j", "x", "v", "w", "p", "f", ",", ".", "y", "g", "b", "k", "c", "d"};
    private String[] numMap = new String[]{
            "1", "2", "3", ".",         // top left
            "4", "5", "6", ",",         // top right
            "7", "8", "!", "?",         // bottom left
            "9", "0", "@", "#"};       // bottom right

    CharacterTree()
    {

    }


    CharacterTree(boolean chars)
    {
        String charArray[];
        if(chars){
            charArray = charMap;
        }
        else{
            charArray = numMap;
        }
        //Create array of TreeNode leaves
        TreeNode[] nodeArray = new TreeNode[charArray.length];

        //Add an assert statement to say charArray.length() = 28

        //Initialise all leaves
        for (int i=0; i<charArray.length; i++)
            nodeArray[i] = new TreeNode(charArray[i], true);

        //Create and initialise branches
        if(chars){
        TreeNode tl = new TreeNode(null, false, nodeArray[0], nodeArray[1], nodeArray[2], nodeArray[3]);
        TreeNode tr = new TreeNode(null, false, nodeArray[4], nodeArray[5], nodeArray[6], nodeArray[7]);
        TreeNode bl = new TreeNode(null, false, nodeArray[8], nodeArray[9], nodeArray[10], nodeArray[11]);
        TreeNode br = new TreeNode(null, false, null, null, null, null);

        br.topLeft = new TreeNode(null, false, nodeArray[12], nodeArray[13], nodeArray[14], nodeArray[15]);
        br.topRight = new TreeNode(null, false, nodeArray[16], nodeArray[17], nodeArray[18], nodeArray[19]);
        br.bottomLeft = new TreeNode(null, false, nodeArray[20], nodeArray[21], nodeArray[22], nodeArray[23]);
        br.bottomRight = new TreeNode(null, false, nodeArray[24], nodeArray[25], nodeArray[26], nodeArray[27]);
            root = new TreeNode(null, false, tl, tr, bl, br);
        }
        else{
            TreeNode tl = new TreeNode(null, false, nodeArray[0], nodeArray[1], nodeArray[2], nodeArray[3]);
            TreeNode tr = new TreeNode(null, false, nodeArray[4], nodeArray[5], nodeArray[6], nodeArray[7]);
            TreeNode bl = new TreeNode(null, false, nodeArray[8], nodeArray[9], nodeArray[10], nodeArray[11]);
            TreeNode br = new TreeNode(null, false, nodeArray[12], nodeArray[13], nodeArray[14], nodeArray[15]);
            root = new TreeNode(null, false, tl, tr, bl, br);
        }

        //Initialise pointer
        pointer = root;

        //Initialise output buffer
        outputBuffer = new StringBuffer();
    }

    public class TreeNode
    {
        String data;
        boolean leaf;
        TreeNode topLeft;
        TreeNode topRight;
        TreeNode bottomLeft;
        TreeNode bottomRight;

        TreeNode(String data, boolean leaf)
        {
            this.data = data;
            this.leaf = leaf;
        }

        TreeNode(String data, boolean leaf, TreeNode topLeft, TreeNode topRight, TreeNode bottomLeft, TreeNode bottomRight)
        {
            this.data = data;
            this.leaf = leaf;
            this.topLeft = topLeft;
            this.topRight = topRight;
            this.bottomLeft = bottomLeft;
            this.bottomRight = bottomRight;
        }
        public boolean getLeaf(){
            return pointer.leaf;
        }

    }



    public String onTopLeftSwipe()
    {
        pointer = pointer.topLeft;

        if (pointer.leaf)
        {
            String result = pointer.data;
            outputBuffer.append(result);
            pointer = root;
            return result;
        }
        else
            return null;
    }

    public String onTopRightSwipe()
    {
        pointer = pointer.topRight;

        if (pointer.leaf)
        {
            String result = pointer.data;
            pointer = root;
            return result;
        }
        else
            return null;
    }

    public String onBottomLeftSwipe()
    {
        pointer = pointer.bottomLeft;

        if (pointer.leaf)
        {
            String result = pointer.data;
            pointer = root;
            return result;
        }
        else
            return null;
    }

    public String onBottomRightSwipe()
    {
        pointer = pointer.bottomRight;

        if (pointer.leaf)
        {
            String result = pointer.data;
            pointer = root;
            return result;
        }
        else
            return null;
    }

    public void resetPointer()
    {
        pointer = root;
    }

    public String print()
    {
        StringBuffer outputBuffer = new StringBuffer();
        print(root, outputBuffer);
        return outputBuffer.toString();
    }

    public StringBuffer print(TreeNode subtree, StringBuffer outputBuffer)
    {

        if (subtree != null)
        {
            if (subtree.data != null)
            {
                outputBuffer.append(subtree.data);
                outputBuffer.append(" ");
            }

            print(subtree.topLeft, outputBuffer);
            print(subtree.topRight, outputBuffer);
            print(subtree.bottomLeft, outputBuffer);
            print(subtree.bottomRight, outputBuffer);
        }
        return outputBuffer;
    }

    public boolean getLeaf(){
        return pointer.leaf;
    }


}
