package com.example.sam.blutoothsocketreceiver.firebase_classes;

import com.google.firebase.database.IgnoreExtraProperties;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;

/**
 * Created by citruscircuits on 1/17/16
 */
@IgnoreExtraProperties
public class CalculatedMatchData extends Object {
    public Integer predictedRedScore;
    public Integer predictedBlueScore;
    public Float redWinChance;
    public Float blueWinChance;

}