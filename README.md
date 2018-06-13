server api 사용법
사진이 함께 있는 ppt 버전 (바로보기)
https://www.polarisoffice.com/d/2RQBm7C3


서버에 기기등록 & 서버상태 확인
GET swp3.gonetis.com:8888/register/안드로이드ID


전체 사용자 불러오기 (관리자용)
GET swp3.gonetis.com:8888/users/


내 디바이스의 아이들 불러오기
GET swp3.gonetis.com:8888/users/안드로이드ID


내 디바이스의 아이를 추가하기
POST swp3.gonetis.com:8888/users/안드로이드ID


내 디바이스 태그들 해제
DELETE swp3.gonetis.com:8888/users/안드로이드ID


서버 재시작
GET swp3.gonetis.com:8888/restart

지도 받아오기
GET http://swp3.gonetis.com:8888/map/지도이름

