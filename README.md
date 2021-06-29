# SDKSample

## sample
- app: 기본 샘플앱
- view: 샘플 앱에서 사용하는 뷰
## sdk
- libgaze-release: Gaze 모듈
- gazetracker-release: Gaze를 관리하고 가져온 시선 정보를 가공해 전달하는 모듈

### 난독화하려면 proguard-rules.pro에 추가해야함
```
-keep interface camp.visual.libgaze.callbacks.jni.LibGazeJNICallback {
  *;
}
```

5점 calibration : 시간이 조금 더 걸림.
oncalibrationnext: 빨간 점 보여줌..
oncalibration 0: 이 점에 대한 정보 다 모았다고 얘기.

