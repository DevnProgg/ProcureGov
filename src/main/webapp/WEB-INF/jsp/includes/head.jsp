
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<meta charset="UTF-8"/>
<meta name="viewport" content="width=device-width, initial-scale=1.0"/>
<title>${not empty pageTitle ? pageTitle : 'ProcureGov — Federal Tender Management'}</title>

<!-- Google Fonts -->
<link rel="preconnect" href="https://fonts.googleapis.com"/>
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin/>
<link href="https://fonts.googleapis.com/css2?family=Newsreader:ital,opsz,wght@0,6..72,200..800;1,6..72,200..800&family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet"/>

<!-- Material Symbols-->
<link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&display=swap" rel="stylesheet"/>

<!-- ProcureGov Design System -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/procuregov.css"/>

<!-- Tailwind -->
<script src="https://cdn.tailwindcss.com?plugins=forms,container-queries"></script>
<script>
  tailwind.config = {
    darkMode: "class",
    theme: {
      extend: {
        colors: {
          "primary":                    "#003f87",
          "primary-container":          "#0056b3",
          "primary-fixed":              "#d7e2ff",
          "primary-fixed-dim":          "#acc7ff",
          "inverse-primary":            "#acc7ff",
          "on-primary":                 "#ffffff",
          "on-primary-fixed":           "#001a40",
          "on-primary-fixed-variant":   "#004491",
          "on-primary-container":       "#bbd0ff",
          "secondary":                  "#006e25",
          "secondary-container":        "#80f98b",
          "secondary-fixed":            "#83fc8e",
          "secondary-fixed-dim":        "#66df75",
          "on-secondary":               "#ffffff",
          "on-secondary-fixed":         "#002106",
          "on-secondary-fixed-variant": "#00531a",
          "on-secondary-container":     "#007327",
          "tertiary":                   "#722b00",
          "tertiary-container":         "#983c00",
          "tertiary-fixed":             "#ffdbcc",
          "tertiary-fixed-dim":         "#ffb694",
          "on-tertiary":                "#ffffff",
          "on-tertiary-fixed":          "#351000",
          "on-tertiary-fixed-variant":  "#7b2f00",
          "on-tertiary-container":      "#ffc2a7",
          "error":                      "#ba1a1a",
          "error-container":            "#ffdad6",
          "on-error":                   "#ffffff",
          "on-error-container":         "#93000a",
          "background":                 "#f8f9fa",
          "surface":                    "#f8f9fa",
          "surface-bright":             "#f8f9fa",
          "surface-dim":                "#d9dadb",
          "surface-tint":               "#115cb9",
          "surface-variant":            "#e1e3e4",
          "surface-container-lowest":   "#ffffff",
          "surface-container-low":      "#f3f4f5",
          "surface-container":          "#edeeef",
          "surface-container-high":     "#e7e8e9",
          "surface-container-highest":  "#e1e3e4",
          "on-surface":                 "#191c1d",
          "on-surface-variant":         "#424752",
          "on-background":              "#191c1d",
          "outline":                    "#727784",
          "outline-variant":            "#c2c6d4",
          "inverse-surface":            "#2e3132",
          "inverse-on-surface":         "#f0f1f2"
        },
        borderRadius: {
          DEFAULT: "0.125rem",
          lg:      "0.25rem",
          xl:      "0.5rem",
          full:    "0.75rem"
        },
        fontFamily: {
          headline: ["Newsreader", "serif"],
          body:     ["Inter", "sans-serif"],
          label:    ["Inter", "sans-serif"]
        }
      }
    }
   };
</script>
