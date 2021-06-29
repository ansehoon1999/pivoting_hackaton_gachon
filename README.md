가치 봄
=================================
## "가치봄"이란?
"가치 봄"은 시선 추적 기술을 이용한 전자책 리더 앱으로, '가치봄'이라는 이름의 뜻은 “같이 봄(Read Together)”과 “가치 있는 봄(Read Value)”이라는 두가지 의미를 가지고 있으며, "가치봄"을 통해 아이들이 책과 더 가까워지고 책에 흥미를 갖게 되는 가치있는 시간을 가지길 바라는 마음이 담긴 이름이다. 

## 주요 기능
### 1) Look Up the Word(단어 팝업)
> ![image](https://user-images.githubusercontent.com/64013256/123594850-2623b600-d82b-11eb-842d-71b6f1d9b05d.png) 
> 
> * 모르는 단어를 일정시간 이상 응시 하면 해당 단어의 뜻을 팝업으로 출력.
> 
> * **Google Cloud Vison API**를 이용하여 이미지 상의 텍스트를 인식하고(OCR), **시선 추적 API**를 이용하여 사용자의 시선이 멈춘 위치를 파악하여 해당 위치에 있는 단어를 읽어온다. 이후 **Naver Papago API**를 통해 번역하여 팝업으로 단어의 뜻을 보여준다.
### 2) Page Flipper
> ![image](https://user-images.githubusercontent.com/64013256/123595206-9b8f8680-d82b-11eb-9016-aa833245e394.png)
>
> * 글자를 포함하고 있지 않은 여백 부분을 응시하면 페이지를 자동으로 넘겨준다. 쪽수를 기준으로 왼쪽을 응시하면 앞장으로 넘어가고, 오른쪽을 응시하면 뒷장으로 넘어감.
> 
> * **시선 추적 API**를 사용하여 사용자 시선의 좌표를 얻고 그 위치가 텍스트가 포함되지 않은 영역에 일정시간 이상 머무를 경우 좌표 범위에 따라 다음 페이지 이미지를 불러온다.
### 3) Analysis of Reading Attitude
> ![image](https://user-images.githubusercontent.com/64013256/123596415-21f89800-d82d-11eb-8b4e-f9fe6f63f99d.png)
>
> * 사용자가 앱을 이용할 때 **시선 추적 API**를 통하여 사용자의 독서 태도 데이터를 측정한 후 시선의 움직임 속도, 거리, 독서 집중도를 계산하여 차트로 보여준다. 

## 기대 효과 및 발전 가능성
![image](https://user-images.githubusercontent.com/64013256/123596805-95020e80-d82d-11eb-9947-e46d493f806a.png)

## Team Members
![image](https://user-images.githubusercontent.com/64013256/123596903-ad722900-d82d-11eb-9820-0e00d49121f8.png)

## 참고사항
### SDKSample

### sample
- app: 기본 샘플앱
- view: 샘플 앱에서 사용하는 뷰
### sdk
- libgaze-release: Gaze 모듈
- gazetracker-release: Gaze를 관리하고 가져온 시선 정보를 가공해 전달하는 모듈

#### proguard-rules.pro에 추가 필요(난독화를 위함)
```
-keep interface camp.visual.libgaze.callbacks.jni.LibGazeJNICallback {
  *;
}
```

### Calibration관련 method
- 5개점 calibration : 시간이 조금 더 걸림.
- oncalibrationnext: 빨간 점 보여줌.
- oncalibration 0: 이 점에 대한 정보 다 모았다고 얘기.

