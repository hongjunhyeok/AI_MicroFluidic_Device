[app] - Main code가 포함된 핵심코드 모듈
[opencv] - 영상처리용 모듈


# AI_MicroFluidic_Device

## [ELISA]

- 측정하고자 하는 단백질(바이러스)을 항체와 반응시켜 특수한 처리절차를 통해 그 결과를 화학/전류 특성을 통해 확인하는 진단방식

- 일반적으로 단백질(바이러스)의 농도가 높을수록 반응하는 색(color)이 진해지거나 전류의 세기가 증가한다.

![image](https://user-images.githubusercontent.com/34786411/105135878-12657c80-5b34-11eb-902a-2b3b1db3206f.png)


## [기존방식의 한계]

- ELISA 실험의 경우 수동으로 조작하기 때문에 다음과 같은 문제점이 발생
    1. Sample Volume을 정확하게 제어하기 어려움
    2. Sample React time을 정확하게 제어하기 어려움
    3. 절차가 복잡하기 때문에 많은 시간과 시약의 낭비가 발생
    
    
## [제안]

- 이러한 한계를 해결하기위해 반 자동화된 플랫폼기기를 제작하여 시스템이 다음과 같은 방식으로 실험을 진행함.
    1. Machine Learning을 통한 Object Detection으로 시약샘플의 위치, 이동현황을 파악
    2. 적절한 반응이 일어나도록 유체를 제어
    3. 정상적으로 반응이 일어난 후, 이를 정량화하여 수치를 시각화 및 데이터베이스에 저장.
    
    
    [시스템 컨셉디자인]
   
    ![image](https://user-images.githubusercontent.com/34786411/105132772-d2e86180-5b2e-11eb-8ad7-905079a3a2ed.png)
    

## [원리]

- 스마트폰 환경에서 실시간으로 유체제어 및 사물인식을 진행해야 하므로 연산의 효율화가 중요함. 이를 위해 두가지 알고리즘 적용

   1. ROI Cascading 알고리즘
   
   - 관심영역을 화면전체-> 반응챔버로 점차적으로 줄여나가면서 연산량을 줄여나가는 방식
   - Dust, Shadow 같은 Noise가 필터링되기 때문에 오차 감소, 시스템 동작속도(FPS) 증가
    
  
   **동영상**
   
  [![클릭시  ](https://img.youtube.com/vi/HG8uFNL7eg0/0.jpg)](https://youtu.be/HG8uFNL7eg0?t=0s)
  
  ![image](https://user-images.githubusercontent.com/34786411/105135263-193fbf80-5b33-11eb-89ce-7ce165ab4783.png)
 
   (a) -> (c)로 진행되면서 빨간테두리 내부만 연산이 진행되는 알고리즘
 
 2. Conditional Activation 알고리즘
  
   - 유체의 흐름을 제어하기 위해서 **6종의 class를 나누어 인식**해야하지만 불필요한 시스템자원을 소모함.
   
   
   - 이를 위해 1종 * 6개의 classifier를 만들고 각 classifier의 순서를 도식화하여 불필요한 classifier의 연산을 lock하였음.
   ( 매 프레임마다 6종의 object 인식 --> 매 프레임마다 1~2종의 object 인식 )
   
   - 각 classifier는 유기적으로 타 classifier의 동작을 on/off하므로 꼭 필요한 연산만을 진행함. 아래는 이를 도식화한 테이블
   
   ![image](https://user-images.githubusercontent.com/34786411/105135565-979c6180-5b33-11eb-929a-26c533c90738.png)
   
  
  3. Classifier 모델 훈련 
  
  - Classifier는 Haar-Cascade 알고리즘을 이용하여 훈련하였음.
  
  ![image](https://user-images.githubusercontent.com/34786411/105135771-e9dd8280-5b33-11eb-8d05-3b6068860acb.png)
  
  훈련절차는 다음과 같다.
  
    1. 전처리 - 샘플 데이터 생성 
    2. 전처리 - 샘플 데이터 라벨링
    3. 훈련   - 샘플 데이터와 라벨링을 이용해 벡터파일 생성
    4. 훈련   - 벡터파일을 이용해 Training procedure 진행 및 optimizing
    5. 평가   - 훈련된 classifier model의 정확도 측정
    6. 최적화 - classifier model 업데이트 및 hyper-parameter 조정 
        
    1.의 경우 실제 촬영한 동영상을 이용하여 단일 이미지로 split 하여 약 3만장의 이미지를 추출하였다.
    2.의 경우 화면 중앙에 목표한 object가 오도록 동영상 편집을 진행하여 자동적으로 라벨링이 될 수 있도록 코드를 제작하였다.
    3.의 경우 Ubuntu.16.04환경에서 OPENCV 라이브러리를 이용하여 CREATE_SAMPLES 기능을 사용하였다.
    4.의 경우 실험을 통하여 적절한 parameter를 조정하여 훈련설정하였다.
    5.의 경우 훈련중 임시제작된 모델파일을 이용하여 정확도를 측정,비교 후 우수한 모델을 채택하였다.
    6.의 경우 시뮬레이터를 제작하여 정확도를 평가한 후 적절한 파라미터를 조정하여 모델최적화를 진행하였다.
  ![2번항목코드 autoLabeling cvtfile.py](https://github.com/hongjunhyeok/opencv)
  ![6번항목코드 simulator main.py](https://github.com/hongjunhyeok/opencv)
   
  
  ## [결과] 
  
  - MainActivity.java를 실행시키면 다음과 같은 화면을 볼 수 있다.
  각화면의 상세내역은 다음과 같다.
  
  ![image](https://user-images.githubusercontent.com/34786411/105136372-e0084f00-5b34-11eb-8b3a-ddf2e5ec4909.png)

    
  **동영상**
  
  [![클릭시 자동재생](https://img.youtube.com/vi/VxlukVH63bI/0.jpg)](https://youtu.be/VxlukVH63bI?t=0s)



  - 알고리즘 적용 유무에 따라 속도향상과 정확도를 각각 측정하였다.
  -----------
  1. PC에서는 약 5배 이상의 속도개선이 이루어졌고 정확도 역시 알고리즘 적용전과 비해 우수하게 측정되었다.
  2. 스마트폰환경에서는 약 8배 이상의 속도개선이 이루어졌다 정확도 역시 알고리즘 적용전과 비해 우수하게 측정되었다.
  
  - 이러한 원인으로는 다음과 같이 파악된다.
  -------------
  1. ROI-cascading 알고리즘을 통해 노이즈가 감소하여 에러가 낮아졌고 연산량이 약 90% 감소.
  2. Conditional-Activation 알고리즘을 통해 연산량이 66% 감소
  

  
  
  

  ## [결론]
 
 본 프로젝트 의의
  1. AI기반 자동화된 유체제어를 통해 복잡한 절차를 가진 실험을 자동화하였다.
  2. 실시간으로 처리가 가능하도록 알고리즘을 도입, 스마트폰 내부에서도 끊김없이 동작이 가능하다.
  3. 측정된 결과를 통해 데이터베이스로 전송이 가능하며 이는 추후 모델훈련에 있어서 점진적으로 성능이 증가할 것으로 판단한다.
 
 
 한계
  1. 여전히 카메라기반 동작으로 인해 빛의 조도, noise가 정확도에 있어서 큰 영향을 미친다.
  2. 모델훈련에 있어서 복잡한 절차로 인해 업데이트의 난이도가 존재한다. 간소화할 필요가 있다.
  3. 해당 프로젝트의 코드는 스마트폰의 기종에 따라 성능이 변경될 수 있다.



