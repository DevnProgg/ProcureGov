
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Supplier Enrollment | ProcureGov" scope="request" />

<!DOCTYPE html>
<html lang="en" class="light">
<head>
    <%@ include file="/WEB-INF/jsp/includes/head.jsp" %>
    <style>
        .material-symbols-outlined {
            font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24;
        }
    </style>
</head>
<body class="bg-surface-container-low text-on-surface antialiased">

<header class="fixed top-0 w-full z-50 bg-white/85 backdrop-blur-md shadow-[0_20px_40px_rgba(25,28,29,0.05)]">
    <div class="flex justify-between items-center px-8 h-20 max-w-full mx-auto">
        <div class="text-2xl font-serif font-bold tracking-tight text-blue-900">Procurement Gov</div>
        <nav class="hidden md:flex items-center gap-8">
            <a class="text-slate-600 hover:text-blue-800 transition-colors font-sans text-sm font-medium" href="#">Tenders</a>
            <a class="text-slate-600 hover:text-blue-800 transition-colors font-sans text-sm font-medium" href="#">Insights</a>
            <a class="text-slate-600 hover:text-blue-800 transition-colors font-sans text-sm font-medium" href="#">Policy</a>
        </nav>
        <div class="flex items-center gap-4">
            <a href="${pageContext.request.contextPath}/auth/login"
               class="px-6 py-2 bg-primary text-on-primary rounded-xl font-medium text-sm transition-all active:scale-95">
               Sign In
            </a>
        </div>
    </div>
</header>

<main class="pt-32 pb-20 max-w-7xl mx-auto px-6 lg:px-12">
    <div class="mb-16">
        <p class="font-label text-secondary text-sm font-bold tracking-widest uppercase mb-4">Official Federal Registry</p>
        <h1 class="text-5xl lg:text-7xl font-headline font-bold text-primary mb-6 leading-tight">Supplier Enrollment</h1>
        <div class="max-w-2xl">
            <p class="text-on-surface-variant text-lg leading-relaxed italic border-l-4 border-secondary-container pl-6 py-2">
                Establishing a new standard for federal partnerships. Enrollment ensures your entity is cataloged within the national procurement ecosystem.
            </p>
        </div>
    </div>

    <c:if test="${not empty registrationError}">
        <div class="pg-alert pg-alert--error mb-8" role="alert">
            <span class="material-symbols-outlined">error</span>
            <span>${registrationError}</span>
        </div>
    </c:if>

    <%-- Updated form section of register.jsp --%>
    <form action="${pageContext.request.contextPath}/auth/register" method="post">
        <div class="grid grid-cols-1 lg:grid-cols-12 gap-12">
            <div class="lg:col-span-8 space-y-12">

                <nav class="flex items-center gap-4 py-4 px-6 bg-surface-container-lowest rounded-xl shadow-[0_20px_40px_rgba(25,28,29,0.02)]">
                    <div class="flex items-center gap-2 text-primary font-bold">
                        <span class="w-8 h-8 rounded-full bg-primary text-on-primary flex items-center justify-center text-xs">01</span>
                        <span class="text-sm font-label uppercase tracking-wider">Entity & Account</span>
                    </div>
                </nav>

                <section class="bg-surface-container-lowest p-10 rounded-xl shadow-[0_20px_40px_rgba(25,28,29,0.03)] space-y-8">
                    <div class="flex items-baseline gap-4 mb-8">
                        <h2 class="text-3xl font-headline font-semibold text-on-surface">Company Details</h2>
                    </div>
                    <div class="grid grid-cols-1 md:grid-cols-2 gap-8">
                        <div class="space-y-2">
                            <label class="block text-xs font-label font-bold uppercase text-slate-500 tracking-tighter" for="businessName">Legal Business Name</label>
                            <input name="businessName" id="businessName" class="w-full bg-surface-container-highest border-none rounded-xl px-4 py-3 focus:ring-2 focus:ring-primary" type="text" value="${param.businessName}" required/>
                        </div>
                        <div class="space-y-2">
                            <label class="block text-xs font-label font-bold uppercase text-slate-500 tracking-tighter" for="reg_number">Registration Number</label>
                            <input name="reg_number" id="reg_number" class="w-full bg-surface-container-highest border-none rounded-xl px-4 py-3 focus:ring-2 focus:ring-primary" type="text" value="${param.reg_number}" required/>
                        </div>
                        <div class="md:col-span-2 space-y-2">
                            <label class="block text-xs font-label font-bold uppercase text-slate-500 tracking-tighter" for="address">Headquarters Address</label>
                            <textarea name="address" id="address" rows="2" class="w-full bg-surface-container-highest border-none rounded-xl px-4 py-3 focus:ring-2 focus:ring-primary" required>${param.address}</textarea>
                        </div>
                    </div>
                </section>

                <section class="bg-surface-container-lowest p-10 rounded-xl shadow-[0_20px_40px_rgba(25,28,29,0.03)] space-y-8">
                    <div class="flex items-baseline gap-4 mb-8">
                        <h2 class="text-3xl font-headline font-semibold text-on-surface">Contact Information</h2>
                    </div>
                    <div class="grid grid-cols-1 md:grid-cols-2 gap-8">
                        <div class="space-y-2">
                            <label class="block text-xs font-label font-bold uppercase text-slate-500 tracking-tighter" for="email">Official Email Address</label>
                            <input name="email" id="email" class="w-full bg-surface-container-highest border-none rounded-xl px-4 py-3 focus:ring-2 focus:ring-primary" type="email" value="${param.email}" required/>
                        </div>
                        <div class="space-y-2">
                            <label class="block text-xs font-label font-bold uppercase text-slate-500 tracking-tighter" for="phone_number">Phone Number</label>
                            <input name="phone_number" id="phone_number" class="w-full bg-surface-container-highest border-none rounded-xl px-4 py-3 focus:ring-2 focus:ring-primary" type="tel" value="${param.phone_number}" required/>
                        </div>
                    </div>
                </section>

                <section class="bg-surface-container-lowest p-10 rounded-xl shadow-[0_20px_40px_rgba(25,28,29,0.03)] space-y-8">
                    <div class="flex items-baseline gap-4 mb-8">
                        <h2 class="text-3xl font-headline font-semibold text-on-surface">Account Credentials</h2>
                    </div>
                    <div class="grid grid-cols-1 md:grid-cols-2 gap-8">
                        <div class="space-y-2">
                            <label class="block text-xs font-label font-bold uppercase text-slate-500 tracking-tighter" for="username">Username</label>
                            <input name="username" id="username" class="w-full bg-surface-container-highest border-none rounded-xl px-4 py-3 focus:ring-2 focus:ring-primary" type="text" required/>
                        </div>
                        <div class="space-y-2">
                            <label class="block text-xs font-label font-bold uppercase text-slate-500 tracking-tighter" for="password">Password</label>
                            <input name="password" id="password" class="w-full bg-surface-container-highest border-none rounded-xl px-4 py-3 focus:ring-2 focus:ring-primary" type="password" required/>
                        </div>
                    </div>
                </section>

                <div class="pt-6">
                    <button type="submit" class="w-full py-6 bg-gradient-to-br from-primary to-primary-container text-on-primary rounded-xl font-headline text-2xl font-bold shadow-lg active:scale-95 transition-all">
                        Register Entity
                    </button>
                </div>

            </div>
            <aside class="lg:col-span-4 space-y-8">
                <div class="bg-surface-container p-8 rounded-xl border-l-8 border-primary">
                    <h3 class="text-xl font-headline font-bold text-primary mb-6">Enrollment Guidelines</h3>
                    <ul class="space-y-4 text-sm text-on-surface-variant">
                        <li class="flex gap-3">
                            <span class="material-symbols-outlined text-primary scale-75">verified</span>
                            <span>Legal names must match Bureau of Internal Revenue records.</span>
                        </li>
                        <li class="flex gap-3">
                            <span class="material-symbols-outlined text-primary scale-75">verified</span>
                            <span>Processing time: 5-10 business days.</span>
                        </li>
                    </ul>
                </div>

                <div class="p-4 bg-secondary-container/20 rounded-lg">
                    <div class="flex items-center gap-2 mb-2">
                        <span class="material-symbols-outlined text-secondary text-sm">support_agent</span>
                        <span class="text-xs font-bold text-on-secondary-container uppercase">Support</span>
                    </div>
                    <p class="text-sm font-medium text-secondary">Call 1-800-GOV-PROC</p>
                </div>
            </aside>
        </div>
    </form>
</main>

<%-- Include common footer --%>
<%@ include file="/WEB-INF/jsp/includes/footer_public.jsp" %>

</body>
</html>