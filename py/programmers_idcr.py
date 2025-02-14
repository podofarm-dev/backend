from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.chrome.options import Options
from bs4 import BeautifulSoup
import time

# ChromeDriver 설정
options = Options()
options.add_argument("--lang=ko")
options.add_argument("--headless")
options.add_argument("--disable-gpu")
options.add_argument("--no-sandbox")

service = Service(r"C:\Program Files\chromedriver\chromedriver.exe")
driver = webdriver.Chrome(service=service, options=options)

base_url = "https://school.programmers.co.kr/learn/challenges?order=acceptance_desc&levels=1%2C2&languages=java&page="
page = 1
lesson_ids = []  # lesson_id만 저장할 리스트

# 크롤링 시작
while True:
    url = f"{base_url}{page}"
    driver.get(url)
    time.sleep(2)

    html = driver.page_source
    soup = BeautifulSoup(html, "html.parser")

    bookmarks = soup.select("div.bookmark a")

    if not bookmarks:
        print("No more data found. Ending scraping.")
        break

    for bookmark in bookmarks:
        href = bookmark.get("href")
        if href:
            lesson_id = href.split("/")[-1]
            lesson_ids.append(lesson_id)

    page += 1
    if page > 10:  # 페이지 수 제한
        break

driver.quit()

# lesson_id 출력 (숫자 형식으로 변환)
for lesson_id in lesson_ids:
    print(int(lesson_id))
