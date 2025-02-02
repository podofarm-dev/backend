from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.chrome.options import Options
from bs4 import BeautifulSoup
import sys
import time

# Python 출력 인코딩 UTF-8로 설정
sys.stdout.reconfigure(encoding='utf-8')

# ChromeDriver 경로 설정 및 옵션
options = Options()
options.add_argument("--lang=ko")  # 브라우저 언어를 한국어로 설정
options.add_argument("--headless")  # 브라우저 창을 표시하지 않음
options.add_argument("--disable-gpu")  # GPU 사용 안 함
options.add_argument("--no-sandbox")  # 샌드박스 모드 비활성화

service = Service(r"C:\Program Files\chromedriver\chromedriver.exe")  # Chromedriver 경로
driver = webdriver.Chrome(service=service, options=options)

# 기본 URL 설정
base_url = "https://school.programmers.co.kr/learn/challenges?order=acceptance_desc&levels=1%2C2&languages=java&page="

# 페이지 탐색
page = 1
while True:
    url = f"{base_url}{page}"
    driver.get(url)
    time.sleep(2)  # 페이지 로딩 대기

    # 페이지 소스 가져오기
    html = driver.page_source
    soup = BeautifulSoup(html, "html.parser")

    # 'bookmark' div에서 <a> 태그 추출
    bookmarks = soup.select("div.bookmark a")

    # 'td.level' 내 'span' 태그에서 클래스 이름 추출
    levels = soup.select("td.level span")

    # 'td.acceptance-rate'에서 숫자 추출
    acceptance_rates = soup.select("td.acceptance-rate")

    # 데이터가 없으면 루프 종료
    if not bookmarks:
        print("No more data found. Ending scraping.")
        break

    # 추출한 데이터 출력
    for i, bookmark in enumerate(bookmarks):
        href = bookmark.get("href")
        text = bookmark.text.strip()
        level = levels[i].get("class")[0] if i < len(levels) else "N/A"
        rate = acceptance_rates[i].text.strip() if i < len(acceptance_rates) else "N/A"
        if href:
            lesson_id = href.split("/")[-1]
            print(f"ID: {lesson_id}, 제목: {text}, Level: {level}, Rate: {rate}")

    # 다음 페이지로 이동
    page += 1

# WebDriver 종료
driver.quit()
