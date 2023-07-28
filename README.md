# GoogleMap_search

## 목차
*  📝 [개요](#-개요)
*  🛠 [기술 및 도구](#-기술-및-도구)
*  ✨ [기능 구현](#-기능-구현)

## **📝 개요**
> **프로젝트** : 스마트 지킴이
>
> **인원** : 4인
> 
> **주제** : Google Map을 활용하여 어플을 통한 gps위치 정보를 활용하여 사용자를 지켜주는 시스템입니다.
> 
> **제작 기간** : 2022.09 ~ 2022.12
> 
> **주요 기능**   
> > * Google Map API를 활용하여 화면에 지도를 표시하고 마커와 폴리곤의 기능을 활용합니다.
> > * NEO-7M GPS 데이터 전송 및 위치 표시를 통해 사용자의 위치에 마커를 생성하여 지도상에 표시를 해줍니다.
> > * GPS 위치 이탈 감지 및 경고 기능을 통해 사용자의 위치및 보호가 가능한 기능을 수행합니다.
>
> **참고 포트폴리오 및 논문**
> > * 링크 : https://drive.google.com/drive/folders/12iu5VZvDj3vtFyHB9HT8SHIC3XjxP0fC?usp=sharing



## **🛠 기술 및 도구**   
> **언어** :<img src="https://img.shields.io/badge/Java-007396?style=flat&logo=OpenJDK&logoColor=white"/>, <img alt="cplusplus" src ="https://img.shields.io/badge/cplusplus-00599C.svg?&style=flat-square&logo=cplusplus&logoColor=white"/>  
> **환경** : <img alt="androidstudio" src ="https://img.shields.io/badge/androidstudio-3DDC84.svg?&style=flat-square&logo=androidstudio&logoColor=white"/>, <img alt="arduino" src ="https://img.shields.io/badge/arduino-00979D.svg?&style=flat-square&logo=arduino&logoColor=white"/>  
> **라이브러리** : <img alt="googleassistant" src ="https://img.shields.io/badge/googleassistant-4285F4.svg?&style=flat-square&logo=googleassistant&logoColor=white"/>, <img alt="googlestreetview" src ="https://img.shields.io/badge/googlestreetview-0F9D58.svg?&style=flat-square&logo=googlestreetview&logoColor=white"/>   
> **도구** : <img alt="github" src ="https://img.shields.io/badge/github-181717.svg?&style=flat-square&logo=github&logoColor=white"/>   


## **✨ 기능 구현**
### **블록다이어그램**
<img width="70%" alt="간단 블록도" src="https://github.com/RedCloud79/GoogleMap_search/blob/main/%EB%B8%94%EB%A1%9D%20%EB%8B%A4%EC%9D%B4%EC%96%B4%EA%B7%B8%EB%9E%A8.png" />

> * gps의 위치정보를 받아와 앱에 표시를 해주며, 앱에는 보호구역및 블루투스 연결을 위한 기능이 들어갔있다.

### **플로우차트**
<img width="100%" alt="플로우차트" src="https://github.com/RedCloud79/GoogleMap_search/blob/main/%ED%94%8C%EB%A1%9C%EC%9A%B0%EC%B0%A8%ED%8A%B8.png" />

> * 어플에 대한 동작을 알려주는 플로우 차트입니다.
> * 메인 화면에서 보호구역 설정을 위한 모서리를 마커로 지정을해주면 해당 구역을 폴리곤 으로 영역을 지정해준다.
> * 서브 화면에서 블루투스를 연결하는 동작을 통해 gps의 위치정보를 받아온다.

### **GPS 하드웨어**
<img width="100%" alt="gps 하드웨어" src="https://github.com/RedCloud79/GoogleMap_search/blob/main/GPS%ED%95%98%EB%93%9C%EC%9B%A8%EC%96%B4.PNG" />

> * 왼쪽 사진은 아두이노와 블루투스모듈, Neo-7M을 활용한 하드웨어구성이다.
> * 오른쪽 사진은 Esp-32 cam보드와 Neo-7M을 활용한 하드웨어구성이다.
> * Esp-32보드는 내부 블루투스를 활용하여 블루투스 모듈이 불필요하다.

### **GPS 수신데이터**
<img width="70%" alt="gps 수신데이터" src="https://github.com/RedCloud79/GoogleMap_search/blob/main/GPS%20%EC%88%98%EC%8B%A0%EB%8D%B0%EC%9D%B4%ED%84%B0.PNG" />

> * 오른쪽 사진과 같은 데이터를 gps가 위성과 연결이되면 받아온다.
> * GPGGA의 정보값에 위도,경도,고도 등의 정보를 받아온다.
> * 아두이노, esp에서 gps정보를 lat, lang으로 변환하여 블루투스를 통해 값을 어플로 전송한다.

### **어플 화면구성**
<img width="70%" alt="어플 화면구성" src="https://github.com/RedCloud79/GoogleMap_search/blob/main/%EC%96%B4%ED%94%8C%20%ED%99%94%EB%A9%B4%EA%B5%AC%EC%84%B1.PNG" />

> * 왼쪽은 메인화면이고, 오른쪽은 블루투스 연결 및 정보를 불러오는 서브화면이다.
> * 왼쪽 사진은 보호구역 설정 후 gps가 이탈했을 경우 동작하는 화면이다.
> * 상단의 경고 메시지와 하단에 outside 및 gps의 좌표가 출력된다.
> * 블루투스 연결하는 화면은 핸드폰의 블루투스 기능의 on/off를 제어해주며, 연결하기를 통하여 블루투스 기기 리스트를 보여준다.











