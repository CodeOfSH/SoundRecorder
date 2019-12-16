<!--
 * @Author: CodeOfSH
 * @Github: https://github.com/CodeOfSH
 * @Date: 2019-12-16 14:36:51
 * @LastEditors: CodeOfSH
 * @LastEditTime: 2019-12-16 15:09:11
 * @Description: 
 -->
# SoundRecorder
This is an Android app for recording sound in specific format. We use it for some research sound recording task. You can simply modify it to satisfy other requirement.

## Simple Usage
In first start, it will request permission for microphone and stroage.
In the main page, you could see a radio group to select recording format in Mono or Stereo. Then, use the "start" button to recording. It will create a folder in phone's root directionary and store the audio in PCM and WAV format.

## Suggestion
You can simply modify and make use of the AudioRecorder and PcmToWavUtil class to implement more specific recording funtion.