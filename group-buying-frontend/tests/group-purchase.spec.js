import { test, expect } from '@playwright/test';
import path from 'path';

const TEST_IMAGE_PATH = path.join(process.cwd(), 'tests', 'fixtures', 'test-image.png');

async function signupAndLogin(page) {
    const email = `gp_test_${Date.now()}@test.com`;
    const password = 'password123!';

    await page.goto('/signup');
    await page.getByPlaceholder('이메일').fill(email);
    await page.waitForTimeout(500);
    await page.getByRole('button', { name: '중복 확인' }).click();
    await expect(page.getByText('사용 가능한 이메일입니다')).toBeVisible({ timeout: 10000 });

    await page.getByPlaceholder('비밀번호').first().fill(password);
    await page.getByPlaceholder('비밀번호 확인').fill(password);
    await page.getByPlaceholder('닉네임').fill('GP테스터');

    page.once('dialog', dialog => dialog.accept());
    await page.getByRole('button', { name: '회원가입' }).click();
    await page.waitForTimeout(500);

    await page.goto('/login');
    await page.getByPlaceholder('이메일').fill(email);
    await page.getByPlaceholder('비밀번호').fill(password);
    await page.getByRole('button', { name: '로그인' }).click();
    await expect(page).toHaveURL('/products');

    return { email, password };
}

async function createProduct(page) {
    await page.goto('/products/create');
    await page.locator('label:has-text("상품명") + input').fill('GP테스트상품');
    await page.locator('label:has-text("상품 설명") + textarea').fill('테스트 상품 설명');
    await page.locator('label:has-text("가격") + input').fill('10000');
    await page.locator('label:has-text("재고 수량") + input').fill('100');
    await page.locator('input[type="file"]').setInputFiles(TEST_IMAGE_PATH);
    page.once('dialog', dialog => dialog.accept());
    await page.getByRole('button', { name: '등록하기' }).click();
    await expect(page).toHaveURL('/products');
}

async function createGroupPurchase(page, title) {
    await page.goto('/group-purchases/create');
    await page.waitForLoadState('networkidle');
    await page.locator('select[name="productId"]').selectOption({ label: 'GP테스트상품' });
    await page.getByPlaceholder('공동구매 제목을 입력하세요').fill(title);
    await page.getByPlaceholder('공동구매에 대해 설명해주세요').fill('테스트 설명');
    await page.locator('input[name="targetPrice"]').fill('9000');
    await page.locator('input[name="targetParticipants"]').fill('5');
    await page.locator('input[name="startDt"]').fill('2026-06-01');
    await page.locator('input[name="endDt"]').fill('2026-12-31');
    await page.getByRole('button', { name: '생성하기' }).click();
    await expect(page).toHaveURL(/\/group-purchases\/\d+/);
}

test.describe('공동구매 목록/상세/참여', () => {

    test('공동구매 목록 페이지 로드 확인', async ({ page }) => {
        await page.goto('/group-purchases');
        await page.waitForLoadState('networkidle');
        await expect(page.getByText('공동구매 목록')).toBeVisible({ timeout: 10000 });
    });

    test('비로그인 상태에서 공동구매 생성 버튼이 보이지 않음', async ({ page }) => {
        await page.goto('/group-purchases');
        await page.waitForLoadState('networkidle');
        await expect(page.getByRole('button', { name: '+ 공동구매 생성' })).not.toBeVisible();
    });

    test('로그인 상태에서 공동구매 생성 버튼이 보임', async ({ page }) => {
        await signupAndLogin(page);
        await page.goto('/group-purchases');
        await page.waitForLoadState('networkidle');
        await expect(page.getByRole('button', { name: '+ 공동구매 생성' })).toBeVisible({ timeout: 10000 });
    });

    test('공동구매 생성 후 상세 페이지 이동 확인', async ({ page }) => {
        await signupAndLogin(page);
        await createProduct(page);

        const gpTitle = `테스트공동구매_${Date.now()}`;
        await createGroupPurchase(page, gpTitle);

        await page.waitForLoadState('networkidle');
        await expect(page.getByText(gpTitle)).toBeVisible({ timeout: 10000 });
    });

    test('비로그인 상태에서 상세 페이지 참여 버튼 - 로그인 후 이용 가능', async ({ page }) => {
        await page.goto('/group-purchases');
        await page.waitForLoadState('networkidle');

        const cardCount = await page.locator('.cursor-pointer').count();
        if (cardCount === 0) {
            test.skip();
            return;
        }

        await page.locator('.cursor-pointer').first().click();
        await page.waitForLoadState('networkidle');
        await expect(page.getByRole('button', { name: '로그인 후 이용 가능' })).toBeVisible();
    });

    test('공동구매 생성 후 상세 페이지에서 참여하기 버튼 확인', async ({ page }) => {
        // 다른 유저로 공동구매 생성 후 → 새 유저로 로그인해서 참여
        const creator = await signupAndLogin(page);
        await createProduct(page);

        const gpTitle = `참여테스트_${Date.now()}`;
        await createGroupPurchase(page, gpTitle);
        const gpUrl = page.url();

        // 로그아웃 후 새 계정으로 로그인
        await page.goto('/login');
        const email2 = `joiner_${Date.now()}@test.com`;
        const password2 = 'password123!';

        await page.goto('/signup');
        await page.getByPlaceholder('이메일').fill(email2);
        await page.waitForTimeout(500);
        await page.getByRole('button', { name: '중복 확인' }).click();
        await expect(page.getByText('사용 가능한 이메일입니다')).toBeVisible({ timeout: 10000 });
        await page.getByPlaceholder('비밀번호').first().fill(password2);
        await page.getByPlaceholder('비밀번호 확인').fill(password2);
        await page.getByPlaceholder('닉네임').fill('참여자');
        page.once('dialog', dialog => dialog.accept());
        await page.getByRole('button', { name: '회원가입' }).click();
        await page.waitForTimeout(500);

        await page.goto('/login');
        await page.getByPlaceholder('이메일').fill(email2);
        await page.getByPlaceholder('비밀번호').fill(password2);
        await page.getByRole('button', { name: '로그인' }).click();
        await expect(page).toHaveURL('/products');

        // 공동구매 상세 페이지로 이동
        await page.goto(gpUrl);
        await page.waitForLoadState('networkidle');

        // 참여하기 버튼 확인
        await expect(page.getByRole('button', { name: '참여하기' })).toBeVisible({ timeout: 10000 });

        // 참여
        await page.getByRole('button', { name: '참여하기' }).click();
        await expect(page.getByText('공동구매에 참여하시겠습니까?')).toBeVisible();
        await page.getByRole('button', { name: '확인' }).click();

        // 참여 후 "참여 취소" 버튼으로 변경 확인
        await expect(page.getByRole('button', { name: '참여 취소' })).toBeVisible({ timeout: 10000 });
    });
});