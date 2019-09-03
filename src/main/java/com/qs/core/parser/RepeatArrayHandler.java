package com.qs.core.parser;

import com.qs.core.log.Logger;
import com.qs.core.model.ArrayFormat;
import com.qs.core.model.ParseOptions;

import java.util.*;

// package
class RepeatArrayHandler {
    private Map<String, ArrayRecord> mRecordMap = new LinkedHashMap<>();

    private String mMountedKey = "";
    private boolean mCollecting = false;

    Map<String, ArrayRecord> getRecordMap() {
        return mRecordMap;
    }

    void collectStart(ParseOptions options, QSToken parentToken) {
        mCollecting = true;
        mMountedKey = String.valueOf(parentToken.value);
        ArrayRecord record = mRecordMap.get(String.valueOf(parentToken.value));
        if (record == null) {
            record = new ArrayRecord();
            mRecordMap.put(mMountedKey, record);
        }
        record.increaseIndex(mMountedKey);
    }

    void collectEnd(QSToken token) {
        mCollecting = false;
        ArrayRecord record = mRecordMap.get(mMountedKey);
        if (record == null) {
            record = new ArrayRecord();
            mRecordMap.put(mMountedKey, record);
        }
        record.appendPairValue(token);
        mMountedKey = "";
    }

    boolean isCollecting() {
        return mCollecting;
    }

    static class ArrayRecord {
        private ArrayList<LinkedList<Integer>> mStatusQueueContainer = new ArrayList<>();
        private ArrayList<LinkedList<Object>> mValueQueueContainer = new ArrayList<>();
        private int index = -1;

        void increaseIndex(String key) {
            index++;
            LinkedList<Integer> statusQueue = new LinkedList<>();
            LinkedList<Object> valueQueue = new LinkedList<>();
            statusQueue.addLast(QSParser.S_IN_FINISHED_OBJECT_KEY);
            valueQueue.addLast(key);
            statusQueue.addLast(QSParser.S_IN_FINISHED_ARRAY_INDEX);
            valueQueue.addLast(index);
            mStatusQueueContainer.add(statusQueue);
            mValueQueueContainer.add(valueQueue);
        }

        void appendPairValue(QSToken token) {
            LinkedList<Integer> statusQueue = mStatusQueueContainer.get(mStatusQueueContainer.size() - 1);
            LinkedList<Object> valueQueue = mValueQueueContainer.get(mValueQueueContainer.size() - 1);
            statusQueue.addLast(QSParser.S_IN_FINISHED_VALUE);
            valueQueue.addLast(token.value);
        }

        ArrayList<LinkedList<Integer>> getStatusQueueList() {
            int size = mStatusQueueContainer.size();
            if (size == 0) {
                return new ArrayList<>();
            } else if (size == 1) {
                LinkedList<Integer> statusQueue = mStatusQueueContainer.get(0);
                statusQueue.remove(1);
                return mStatusQueueContainer;
            }
            return mStatusQueueContainer;
        }

        ArrayList<LinkedList<Object>> getValueQueueList() {
            int size = mValueQueueContainer.size();
            if (size == 0) {
                return new ArrayList<>();
            } else if (size == 1) {
                LinkedList<Object> valueQueue = mValueQueueContainer.get(0);
                valueQueue.remove(1);
                String warnMsg = String.format(Locale.CHINA, "qs array \"%s\" mode, only one key-value. converting to object. please check path: %s ", ArrayFormat.REPEAT.getCode(), valueQueue);
                Logger.warn(warnMsg);
            }
            return mValueQueueContainer;
        }
    }
}
