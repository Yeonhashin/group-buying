import { test, expect } from '@playwright/test';

async function signupAndLogin(page) {
    const email = `mypage_test_${Date.now()}@test.com`;
    const password = 'password123!';

    await page.goto('/signup');
    await page.getByPlaceholder('이메일').fill(email);
    await page.waitForTimeout(500);
    await page.getByRole('button', { name: '중복 확인' }).click();
    await expect(page.getByText('사용 가능한 이메일입니다')).toBeVisible({ timeout: 10000 });

    await page.getByPlaceholder('비밀번호').first().fill(password);
    await page.getByPlaceholder('비밀번호 확인').fill(password);
    await page.getByPlaceholder('닉네임').fill('마이테스터');

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

test.describe('마이페이지 - 주문/알림', () => {

    test('마이페이지 접근 - 로그인 상태에서 정상 로드', async ({ page }) => {
        await signupAndLogin(page);
        await page.goto('/mypage');
        await page.waitForLoadState('networkidle');

        await expect(page.getByRole('heading', { name: '마이페이지' })).toBeVisible();
        await expect(page.getByRole('heading', { name: /읽지 않은 알림/ })).toBeVisible();
        // "내 주문"은 주문 없을 때 안 보임 → 빈 상태 메시지로 대체
        await expect(page.getByText('주문 내역이 없습니다.')).toBeVisible();
    });

    test('마이페이지 - 주문 내역 없을 때 빈 상태 표시', async ({ page }) => {
        await signupAndLogin(page);
        await page.goto('/mypage');
        await page.waitForLoadState('networkidle');

        await expect(page.getByText('주문 내역이 없습니다.')).toBeVisible();
    });

    test('마이페이지 - 알림 없을 때 빈 상태 표시', async ({ page }) => {
        await signupAndLogin(page);
        await page.goto('/mypage');
        await page.waitForLoadState('networkidle');

        await expect(page.getByText('읽지 않은 알림이 없습니다.')).toBeVisible();
    });

    test('마이페이지 - 읽은 알림 토글 동작 확인', async ({ page }) => {
        await signupAndLogin(page);
        await page.goto('/mypage');
        await page.waitForLoadState('networkidle');

        await page.getByText(/읽은 알림/).click();
        await expect(page.getByText('▼')).toBeVisible();
    });

    test('비로그인 상태에서 마이페이지 접근 시 로그인 페이지로 리다이렉트', async ({ page }) => {
        await page.goto('/mypage');
        await expect(page).toHaveURL('/login');
    });
});