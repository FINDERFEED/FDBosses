package com.finderfeed.fdbosses.client.boss_screen.text_block.text_block_parser;

import com.finderfeed.fdbosses.client.boss_screen.text_block.TextBlockEntry;
import com.finderfeed.fdbosses.client.boss_screen.text_block.text_block_entries.SimpleTextEntry;
import com.finderfeed.fdlib.data_structures.Pair;
import net.minecraft.ResourceLocationException;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class TextBlockParser {

    public static List<TextBlockEntry> parseComponent(Component component,float textScale){

        List<TextBlockEntry> textBlockEntries = new ArrayList<>();

        List<String> strings = new ArrayList<>();
        List<Style> correspondingStyles = new ArrayList<>();
        component.visit((style,string)->{
            strings.add(string);
            correspondingStyles.add(style);
            return Optional.empty();
        },Style.EMPTY);

        for (int i = 0; i < strings.size();i++){
            String str = strings.get(i);
            Style style = correspondingStyles.get(i);
            parseString(str,style,textScale,textBlockEntries);
        }

        return textBlockEntries;
    }


    private static void parseString(String s,Style stringStyle,float textScale, List<TextBlockEntry> entries){

        StringBuilder reading = new StringBuilder();



        for (int i = 0; i < s.length();i++){
            char character = s.charAt(i);

            if (character == '{'){
                if (i + 1 < s.length() && s.charAt(i + 1) == '{'){

                    Pair<FDString, Integer> pair = parseFDString(s,i);


                    int lastIndex = pair.second;
                    if (lastIndex < s.length()) {
                        s = s.substring(lastIndex);
                    }else{
                        s = "";
                    }
                    i = -1;

                    entries.add(new SimpleTextEntry(FormattedText.of(reading.toString(),stringStyle),textScale));

                    reading = new StringBuilder();

                    FDString string = pair.first;
                    TextBlockProcessor textBlockProcessor = string.getProcessor();
                    var args = string.getArguments();
                    var entriesToAdd = textBlockProcessor.parse(textScale,args);
                    entries.addAll(entriesToAdd);


                    continue;
                }
            }

            reading.append(character);

        }


        if (!reading.isEmpty()){
            entries.add(new SimpleTextEntry(FormattedText.of(reading.toString(),stringStyle),textScale));
        }
    }

    private static Pair<FDString,Integer> parseFDString(String s, int startIndex){
        if (s.charAt(startIndex) != '{' && s.charAt(startIndex + 1) != '{') throw new RuntimeException("You didn't provide an FDString here!");

        int lastIndex = 0;

        //Read whats inside {{ }}
        StringBuilder builder = new StringBuilder();
        try {
            for (int i = startIndex + 2; i < s.length(); i++) {

                char character = s.charAt(i);
                if (character == '}' && s.charAt(i + 1) == '}'){
                    lastIndex = i + 2;
                    break;
                }

                builder.append(character);
            }
        }catch (StringIndexOutOfBoundsException e){
            throw new RuntimeException("FDString incorrect format! It should start with {{ and end with }}! String: " + s);
        }

        String typeAndArguments = builder.toString().replace(" ","");
        builder = new StringBuilder();

        int argumentsStart = 0;

        //Read text processor type
        char c = typeAndArguments.charAt(0);
        if (c != '(') throw new RuntimeException("FDString incorrect format! After the {{ should come the processor type in parenthesis! Example: {{(fdlib:zhopa) arg1 = argval, arg2 = argval2}}. String: " + s);

        int i = 1;

        try {
            while (true) {
                char character = typeAndArguments.charAt(i);
                if (character == ')') {
                    argumentsStart = i + 1;
                    break;
                }
                i++;
                builder.append(character);
            }
        }catch (StringIndexOutOfBoundsException e){
            throw new RuntimeException("FDString incorrect format! Couldn't find closing parenthesis while reading text processor type. String: " + s);
        }

        String processorId = builder.toString();
        ResourceLocation id;
        try{
            id = ResourceLocation.parse(processorId);
        }catch (ResourceLocationException e){
            throw new RuntimeException("Couldn't parse processor id! String: " + s,e);
        }
        TextBlockProcessor processor = TextBlockProcessors.getProcessor(id);
        if (processor == null){
            throw new RuntimeException("Unknown text procossor type: " + processorId);
        }


        //Reading arguments. If nothing is detected return with zero arguments
        if (argumentsStart >= typeAndArguments.length()){
            return new Pair<>(new FDString(processor,new HashMap<>()),lastIndex);
        }

        String args = typeAndArguments.substring(argumentsStart);

        var argmap = readArguments(args);


        return new Pair<>(new FDString(processor,argmap),lastIndex);
    }

    private static HashMap<String,String> readArguments(String arguments){

        HashMap<String,String> argumentMap = new HashMap<>();

        StringBuilder argname = new StringBuilder();
        StringBuilder argument = new StringBuilder();
        boolean readingArgname = true;

        for (int i = 0; i < arguments.length();i++){
            char c = arguments.charAt(i);
            if (readingArgname) {
                if (c != '=') {
                    argname.append(c);
                } else {
                    readingArgname = false;
                }
            }else{
                if (c != ','){
                    argument.append(c);
                }else{
                    argumentMap.put(argname.toString(),argument.toString());
                    argname = new StringBuilder();
                    argument = new StringBuilder();
                    readingArgname = true;
                }
            }
        }

        if (!argname.isEmpty() && !argument.isEmpty()){
            argumentMap.put(argname.toString(),argument.toString());
        }

        return argumentMap;
    }




}
