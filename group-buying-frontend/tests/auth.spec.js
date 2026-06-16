import { test, expect } from '@playwright/test';

test.describe('회원가입', () => {
    test('회원가입 성공 플로우', async ({ page }) => {
        const uniqueEmail = `test${Date.now()}@test.com`;

        await page.goto('/signup');

        // 이메일 입력 후 중복 확인
        await page.getByPlaceholder('이메일').fill(uniqueEmail);
        await page.getByRole('button', { name: '중복 확인' }).click();
        await expect(page.getByText('사용 가능한 이메일입니다')).toBeVisible();

        // 나머지 필드 입력
        await page.getByPlaceholder('비밀번호').first().fill('password123!');
        await page.getByPlaceholder('비밀번호 확인').fill('password123!');
        await page.getByPlaceholder('닉네임').fill('테스트유저');

        // 회원가입 alert 처리
        page.once('dialog', dialog => {
            expect(dialog.message()).toContain('회원가입 성공');
            dialog.accept();
        });

        await page.getByRole('button', { name: '회원가입' }).click();
    });

    test('이메일 중복 확인 없이 제출 시 에러 메시지', async ({ page }) => {
        await page.goto('/signup');

        await page.getByPlaceholder('이메일').fill(`test${Date.now()}@test.com`);
        await page.getByPlaceholder('비밀번호').first().fill('password123!');
        await page.getByPlaceholder('비밀번호 확인').fill('password123!');
        await page.getByPlaceholder('닉네임').fill('테스트유저');

        // 중복 확인 버튼 안 누르고 바로 제출
        await page.getByRole('button', { name: '회원가입' }).click();

        await expect(page.getByText('이메일 중복 확인을 해주세요')).toBeVisible();
    });
});

test.describe('로그인', () => {
    // 헬퍼 함수 - 회원가입 후 이메일/비밀번호 반환
    async function signupTestUser(page) {
        const email = `test${Date.now()}@test.com`;
        const password = 'password123!';

        await page.goto('/signup');
        await page.getByPlaceholder('이메일').fill(email);
        await page.getByRole('button', { name: '중복 확인' }).click();
        await expect(page.getByText('사용 가능한 이메일입니다')).toBeVisible();

        await page.getByPlaceholder('비밀번호').first().fill(password);
        await page.getByPlaceholder('비밀번호 확인').fill(password);
        await page.getByPlaceholder('닉네임').fill('테스트유저');

        page.once('dialog', dialog => dialog.accept());
        await page.getByRole('button', { name: '회원가입' }).click();
        await page.waitForURL('/login');

        return { email, password };
    }

    test('로그인 성공 시 메인 페이지로 리다이렉트', async ({ page }) => {
        const { email, password } = await signupTestUser(page);

        await page.goto('/login');
        await page.getByPlaceholder('이메일').fill(email);
        await page.getByPlaceholder('비밀번호').fill(password);
        await page.getByRole('button', { name: '로그인' }).click();

        await expect(page).toHaveURL('/products');

        const token = await page.evaluate(() => localStorage.getItem('accessToken'));
        expect(token).not.toBeNull();
    });

    test('잘못된 비밀번호로 로그인 시 에러 메시지 노출', async ({ page }) => {
        const { email } = await signupTestUser(page);

        await page.goto('/login');
        await page.getByPlaceholder('이메일').fill(email);
        await page.getByPlaceholder('비밀번호').fill('wrongPassword!');
        await page.getByRole('button', { name: '로그인' }).click();

        await expect(page.locator('text=/실패|올바르지/')).toBeVisible();
    });

    test('필수 입력값 누락 시 검증 메시지 노출', async ({ page }) => {
        await page.goto('/login');
        await page.getByRole('button', { name: '로그인' }).click();

        await expect(page.getByText('이메일을 입력해주세요')).toBeVisible();
        await expect(page.getByText('비밀번호를 입력해주세요')).toBeVisible();
    });
});