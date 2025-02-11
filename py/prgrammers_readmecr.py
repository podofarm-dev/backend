import sys
from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
import time

# Python 출력 인코딩 UTF-8로 설정
sys.stdout.reconfigure(encoding='utf-8')

# ChromeDriver 설정
options = Options()
options.add_argument("--lang=ko")
options.add_argument("--headless")
options.add_argument("--disable-gpu")
options.add_argument("--no-sandbox")

service = Service(r"C:\Program Files\chromedriver\chromedriver.exe")
driver = webdriver.Chrome(service=service, options=options)

# lesson_id 리스트
lesson_ids = [
    12925, 12928, 12931, 12937, 12944, 12954, 87389, 12932, 12912, 12916, 12933, 12934, 12947, 76501, 12910, 12919,
    12943, 86051, 12935, 12948, 70128, 12903, 12922, 77884, 12917, 82612, 12918, 12950, 12939, 12969, 12941, 12951,
    12906, 12909, 12940, 70129, 147355, 12982, 12924, 12930, 68935, 131705, 12911, 12945, 12973, 86491, 12926, 142086,
    42842, 68644, 81301, 134240, 42748, 12915, 132267, 138477, 42885, 12980, 12914, 12953, 12981, 17681, 138476, 159994,
    176963, 12985, 131701, 76502, 131127, 87390, 1845, 12949, 42578, 42747, 136798, 12901, 17680, 42586, 42840, 87946,
    64065, 42577, 42587, 135808, 12921, 12977, 17677, 43165, 161989, 132265, 1844, 42889, 49994, 84512, 133499, 92335,
    340199, 17684, 17687, 154539, 42626, 42584, 12913, 49993, 131704, 17682, 92341, 77484, 140108, 155652, 160586,
    154538, 17686, 42576, 42888, 250125, 12900, 17679, 42862, 77885, 133502, 131128, 42746, 68936, 118667, 42839,
    42583, 68645, 42883, 64061, 178870, 250121, 148653, 118666, 67256, 86971, 152996, 155651, 12899, 72410, 135807,
    159993, 161990, 12978, 72411, 17683, 154540, 12936, 60058, 67257, 77485, 169199, 147354, 81302, 142085, 150370,
    178871, 12905, 12946, 172928, 134239, 62048, 172927, 60057, 140107, 42890, 340212, 176962, 12952, 92334, 150368,
    250137, 340198, 181187, 160585, 181188, 131130, 250136, 12923, 42860, 92342, 340213, 150369, 72412, 87377, 340211,
    388351, 1829, 12902, 258712, 1835, 148652, 258711, 169198, 86052, 250135, 388352, 388353
]

# 모든 lesson_id에 대해 innerHTML 가져오기
for lesson_id in lesson_ids:
    problem_url = f"https://school.programmers.co.kr/learn/courses/30/lessons/{lesson_id}"
    driver.get(problem_url)
    time.sleep(2)

    try:
        WebDriverWait(driver, 10).until(
            EC.presence_of_element_located((By.CSS_SELECTOR, "div.guide-section-description > div.markdown"))
        )
        
        # innerHTML 추출 및 콘솔 출력
        problem_description = driver.find_element(By.CSS_SELECTOR, "div.guide-section-description > div.markdown").get_attribute("innerHTML")
        print(f"const problem_description_{lesson_id} = `{problem_description}`;\n")

    except Exception as e:
        print(f"// Lesson {lesson_id}: ReadMe를 가져오는 데 실패했습니다. 오류: {str(e)}\n")

driver.quit()
