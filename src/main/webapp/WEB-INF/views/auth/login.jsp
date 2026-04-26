
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="pageTitle" value="Sign In | ProcureGov" scope="request" />
<!DOCTYPE html>
<html lang="en" xmlns:c="http://www.w3.org/1999/XSL/Transform">
<head>
    <%@ include file="/WEB-INF/jsp/includes/head.jsp" %>
</head>
<body style="background: var(--color-background);">

<div class="login-layout">

    <!-- Minimal header anchor -->
    <header class="login-header">
        <div style="font-family:var(--font-headline); font-style:italic; font-weight:700; font-size:1.25rem; color:var(--color-primary);">
            ProcurementGov
        </div>
    </header>

    <main style="display:flex; flex:1; padding-top:5rem;">

        <!-- Left: Editorial Hero -->
        <section class="login-hero" style="background: var(--color-surface-container-low); flex:1; position:relative; overflow:hidden;">
            <div style="position:absolute; inset:0; background:linear-gradient(135deg, rgba(0,63,135,0.04) 0%, transparent 60%);">
            <div class="absolute inset-0 z-0">
            <img class="w-full h-full object-cover opacity-10" data-alt="Modern architectural detail of a government building with clean lines, soft shadows, and a blue sky reflected in glass windows" src="https://lh3.googleusercontent.com/aida-public/AB6AXuA3TzCVReBLaS6YnSXerani-S_PMo0gkxrU65gk8OS0x77FwzDZDDGblFxWonKc1rvD-2u8FCOVAODJqLeVvss4riacOeJRYTefQWZHk8J9CB0vXJ7vE0Zq05vsovJG1pPDjv6vLwdLhHX-TB6OWcNBXmgSuhpEoey3ZnrYov4arnwUc3imx5lBoQfM69GUJIfgQa-p6YCRMKT5NLFr11xgJEg93zy8iCDybwNLMJRUS4SCsPaPp4VU9PKvEfDrTk162iQkzPOCkQ"/>
            <div class="absolute inset-0 bg-gradient-to-br from-primary/5 to-transparent"></div>
            </div></div>
            <div style="position:relative; z-index:1; max-width:36rem; padding:2rem;">
                <span style="display:inline-block; padding:0.3rem 0.75rem; margin-bottom:1.5rem; border-radius:var(--radius-full); background:var(--color-secondary-fixed); color:var(--color-on-secondary-fixed-variant); font-size:0.625rem; font-weight:700; letter-spacing:0.12em; text-transform:uppercase;">
                    Official Federal Gateway
                </span>
                <h1 style="font-family:var(--font-headline); font-size:3.5rem; font-weight:700; color:var(--color-primary); line-height:1.1; letter-spacing:-0.02em; margin:0 0 1.5rem;">
                    The Digital Ledger of<br/><em>Federal Procurement</em>.
                </h1>
                <p style="font-size:1.0625rem; color:var(--color-on-surface-variant); line-height:1.7; max-width:28rem; margin:0 0 3rem;">
                    Securing the nation's supply chain through total transparency and institutional accountability. Every contract, every vendor, every cent accounted for.
                </p>
                <div style="display:flex; flex-direction:column; gap:1.5rem;">
                    <div style="display:flex; align-items:flex-start; gap:1rem;">
                        <span class="material-symbols-outlined" style="color:var(--color-secondary); margin-top:0.125rem;">verified_user</span>
                        <div>
                            <div style="font-family:var(--font-headline); font-size:1.125rem; font-weight:600; color:var(--color-primary); margin-bottom:0.25rem;">Role-Based Access Control</div>
                            <div style="font-size:0.8125rem; color:var(--color-outline);">Separate portals for Officers, Evaluators, and Suppliers.</div>
                        </div>
                    </div>
                    <div style="display:flex; align-items:flex-start; gap:1rem;">
                        <span class="material-symbols-outlined" style="color:var(--color-secondary); margin-top:0.125rem;">history_edu</span>
                        <div>
                            <div style="font-family:var(--font-headline); font-size:1.125rem; font-weight:600; color:var(--color-primary); margin-bottom:0.25rem;">Immutable Audit Trail</div>
                            <div style="font-size:0.8125rem; color:var(--color-outline);">Every status change and score submission is time-stamped.</div>
                        </div>
                    </div>
                </div>
            </div>
        </section>

        <!-- Right: Auth Form -->
        <section style="flex:1; display:flex; align-items:center; justify-content:center; padding:2rem; background:var(--color-surface-container-low);">
            <div class="login-card">

                <div style="margin-bottom:2.5rem;">
                    <h2 style="font-family:var(--font-headline); font-size:1.75rem; font-weight:700; color:var(--color-on-surface); margin:0 0 0.375rem;">Access Portal</h2>
                    <p style="font-size:0.875rem; color:var(--color-on-surface-variant); margin:0;">Sign in to continue to your dashboard</p>
                </div>

                <!-- Error -->
                <c:if test="${not empty loginError}">
                    <div class="pg-alert pg-alert--error" style="margin-bottom:1.5rem;" role="alert">
                        <span class="material-symbols-outlined">lock</span>
                        <span>${loginError}</span>
                    </div>
                </c:if>

                <!-- Login Form -->
                <form action="${pageContext.request.contextPath}/auth/login" method="post">
                    <div class="form-group">
                        <label class="form-label" for="email">Work Email</label>
                        <div class="form-input-icon">
                            <input class="form-input"
                                   type="email"
                                   id="email"
                                   name="email"
                                   placeholder="name@agency.gov.ls"
                                   value="${not empty lastEmail ? lastEmail : ''}"
                                   required
                                   autocomplete="email"/>
                            <span class="material-symbols-outlined icon">alternate_email</span>
                        </div>
                    </div>

                    <div class="form-group">
                        <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:0.375rem;">
                            <label class="form-label" for="password" style="margin:0;">Password</label>
                            <a href="${pageContext.request.contextPath}/forgot-password" style="font-size:0.75rem; color:var(--color-primary);">Forgot?</a>
                        </div>
                        <div class="form-input-icon">
                            <input class="form-input"
                                   type="password"
                                   id="password"
                                   name="password"
                                   placeholder="••••••••••••"
                                   required
                                   autocomplete="current-password"/>
                            <span class="material-symbols-outlined icon">key</span>
                        </div>
                    </div>

                    <div class="pg-alert pg-alert--info" style="margin-bottom:1.5rem;">
                        <span class="material-symbols-outlined">info</span>
                        <span>This is a monitored government system. Unauthorised access is an offence under the ICT Act 2012 (Lesotho).</span>
                    </div>

                    <button class="btn btn-primary btn-full btn-lg" type="submit">
                        <span class="material-symbols-outlined">lock_open</span>
                        Authorise Access
                    </button>
                </form>

                <!-- Divider -->
                <div style="display:flex; align-items:center; gap:1rem; margin:1.5rem 0;">
                    <div style="flex:1; height:1px; background:var(--color-surface-container-high);"></div>
                    <span style="font-size:0.6875rem; color:var(--color-outline); text-transform:uppercase; letter-spacing:0.08em;">or</span>
                    <div style="flex:1; height:1px; background:var(--color-surface-container-high);"></div>
                </div>

                <!-- New supplier CTA -->
                <div style="text-align:center;">
                    <p style="font-size:0.8125rem; color:var(--color-on-surface-variant); margin:0 0 0.75rem;">New to ProcureGov?</p>
                    <a href="${pageContext.request.contextPath}/auth/register" class="btn btn-ghost btn-full">
                        <span class="material-symbols-outlined">how_to_reg</span>
                        Register as a Supplier
                    </a>
                </div>

            </div>
        </section>
    </main>

    <%@ include file="/WEB-INF/jsp/includes/footer_public.jsp" %>
</div>
</body>
</html>
