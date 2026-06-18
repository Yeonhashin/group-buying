import { test, expect } from '@playwright/test';
import fs from 'fs';
import path from 'path';

const TEST_IMAGE_PATH = path.join(process.cwd(), 'tests', 'fixtures', 'test-image.png');

test.beforeAll(() => {
    const fixturesDir = path.join(process.cwd(), 'tests', 'fixtures');
    if (!fs.existsSync(fixturesDir)) {
        fs.mkdirSync(fixturesDir, { recursive: true });
    }
    const minimalPng = Buffer.from(
        'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNk+A8AAQUBAScY42YAAAAASUVORK5CYII=',
        'base64'
    );
    fs.writeFileSync(TEST_IMAGE_PATH, minimalPng);
});

async function signupAndLogin(page) {
    const email = `product_test_${Date.now()}@test.com`;
    const password = 'password123!';

    await page.goto('/signup');
    await page.getByPlaceholder('이메일').fill(email);
    await page.waitForTimeout(300);
    await page.getByRole('button', { name: '중복 확인' }).click();
    await expect(page.getByText('사용 가능한 이메일입니다')).toBeVisible({ timeout: 10000 });

    await page.getByPlaceholder('비밀번호').first().fill(password);
    await page.getByPlaceholder('비밀번호 확인').fill(password);
    await page.getByPlaceholder('닉네임').fill('상품테스터');

    page.once('dialog', dialog => {
        console.log('회원가입 ALERT 내용:', dialog.message());
        dialog.accept();
    });
    await page.getByRole('button', { name: '회원가입' }).click();
    await page.waitForTimeout(1000); // 충분히 늘려서 확인

    await page.goto('/login');
    await page.getByPlaceholder('이메일').fill(email);
    await page.getByPlaceholder('비밀번호').fill(password);
    await page.getByRole('button', { name: '로그인' }).click();
    await expect(page).toHaveURL('/products');

    return { email, password };
}

// 상품 등록 폼 채우고 제출하는 헬퍼 (alert 처리 포함)
async function fillAndSubmitProductForm(page, { name, details, price, stock, withFile = true }) {
    // label 뒤에 오는 input을 순서 기반으로 선택
    const nameInput = page.locator('label:has-text("상품명") + input');
    const detailsInput = page.locator('label:has-text("상품 설명") + textarea');
    const priceInput = page.locator('label:has-text("가격") + input');
    const stockInput = page.locator('label:has-text("재고 수량") + input');

    await nameInput.fill(name);
    await detailsInput.fill(details);
    await priceInput.fill(String(price));
    await stockInput.fill(String(stock));

    if (withFile) {
        await page.locator('input[type="file"]').setInputFiles(TEST_IMAGE_PATH);
    }

    page.once('dialog', dialog => dialog.accept());
    await page.getByRole('button', { name: /등록하기|수정하기/ }).click();
}

test.describe('상품 등록/조회/수정/삭제', () => {

    test('상품 등록 후 목록에 노출 확인', async ({ page }) => {
        await signupAndLogin(page);

        const productName = `테스트상품_${Date.now()}`;

        await page.getByRole('button', { name: '+ 상품 등록' }).click();
        await expect(page).toHaveURL('/products/create');

        await fillAndSubmitProductForm(page, {
            name: productName, details: 'E2E 테스트 상품입니다', price: 15000, stock: 10
        });

        await expect(page).toHaveURL('/products');
        await expect(page.getByText(productName)).toBeVisible();
    });

    test('상품 상세 페이지 - 정보 정확히 표시', async ({ page }) => {
        await signupAndLogin(page);

        const productName = `상세테스트_${Date.now()}`;

        await page.getByRole('button', { name: '+ 상품 등록' }).click();
        await fillAndSubmitProductForm(page, {
            name: productName, details: '상세 페이지 확인용 설명', price: 25000, stock: 7
        });

        await expect(page).toHaveURL('/products');
        await page.getByText(productName).click();

        await expect(page.getByRole('heading', { name: productName })).toBeVisible();
        await expect(page.getByText('25,000원')).toBeVisible();
        await expect(page.getByText('재고 7개')).toBeVisible();
    });

    test('본인 상품 수정 - 목록에서 변경 내용 확인', async ({ page }) => {
        await signupAndLogin(page);

        const originalName = `수정전_${Date.now()}`;
        const updatedName = `수정후_${Date.now()}`;

        await page.getByRole('button', { name: '+ 상품 등록' }).click();
        await fillAndSubmitProductForm(page, {
            name: originalName, details: '수정 테스트', price: 10000, stock: 5
        });

        await expect(page).toHaveURL('/products');
        await page.getByText(originalName).click();

        await page.getByRole('button', { name: '수정하기' }).click();
        await expect(page).toHaveURL(/\/products\/\d+\/edit/);

        // 수정 시에는 파일 재업로드 없이 진행 (기존 이미지 유지 케이스)
        await fillAndSubmitProductForm(page, {
            name: updatedName, details: '수정된 설명', price: 30000, stock: 3, withFile: false
        });

        // 수정 후 목록 페이지로 이동, 변경된 이름이 보이는지 확인
        await expect(page).toHaveURL('/products');
        await expect(page.getByText(updatedName)).toBeVisible();
    });

    test('본인 상품 삭제 - 목록에서 사라짐 확인', async ({ page }) => {
        await signupAndLogin(page);

        const productName = `삭제테스트_${Date.now()}`;

        await page.getByRole('button', { name: '+ 상품 등록' }).click();
        await fillAndSubmitProductForm(page, {
            name: productName, details: '삭제될 상품', price: 5000, stock: 1
        });

        await expect(page).toHaveURL('/products');
        await page.getByText(productName).click();

        await page.getByRole('button', { name: '삭제하기' }).click();
        await expect(page.getByText('이 상품을 삭제하시겠습니까?')).toBeVisible();
        await page.getByRole('button', { name: '확인' }).click();

        await expect(page).toHaveURL('/products');
        await expect(page.getByText(productName)).not.toBeVisible();
    });

    test('비로그인 상태에서는 상품 등록 버튼이 보이지 않음', async ({ page }) => {
        await page.goto('/products');

        await expect(page.getByRole('button', { name: '+ 상품 등록' })).not.toBeVisible();
    });
});