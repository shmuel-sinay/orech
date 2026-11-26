.class public final Lcom/android/server/pm/VideoGuard;
.super Ljava/lang/Object;
.source "VideoGuard.java"


# static fields
.field private static final BYPASS_FLAG:Ljava/lang/String; = "/data/system/orech_allow_install"

.field private static final LIBS:[Ljava/lang/String;


# direct methods
.method static constructor <clinit>()V
    .registers 3

    .line 15
    const/16 v0, 0xb

    new-array v0, v0, [Ljava/lang/String;

    const/4 v1, 0x0

    const-string v2, "libvlc"

    aput-object v2, v0, v1

    const/4 v1, 0x1

    const-string v2, "libavcodec"

    aput-object v2, v0, v1

    const/4 v1, 0x2

    const-string v2, "libffmpeg"

    aput-object v2, v0, v1

    const/4 v1, 0x3

    const-string v2, "libmpv"

    aput-object v2, v0, v1

    const/4 v1, 0x4

    const-string v2, "libdav1d"

    aput-object v2, v0, v1

    const/4 v1, 0x5

    const-string v2, "libvpx"

    aput-object v2, v0, v1

    const/4 v1, 0x6

    const-string v2, "libde265"

    aput-object v2, v0, v1

    const/4 v1, 0x7

    const-string v2, "libopenh264"

    aput-object v2, v0, v1

    const/16 v1, 0x8

    const-string v2, "libx264"

    aput-object v2, v0, v1

    const/16 v1, 0x9

    const-string v2, "libijkffmpeg"

    aput-object v2, v0, v1

    const/16 v1, 0xa

    const-string v2, "libijksdl"

    aput-object v2, v0, v1

    sput-object v0, Lcom/android/server/pm/VideoGuard;->LIBS:[Ljava/lang/String;

    return-void
.end method

.method private constructor <init>()V
    .registers 1

    .line 20
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.method public static blocked(Ljava/lang/String;)Z
    .registers 5

    .line 26
    const/4 v0, 0x0

    if-nez p0, :cond_4

    return v0

    .line 28
    :cond_4
    :try_start_4
    new-instance v1, Ljava/io/File;

    const-string v2, "/data/system/orech_allow_install"

    invoke-direct {v1, v2}, Ljava/io/File;-><init>(Ljava/lang/String;)V

    invoke-virtual {v1}, Ljava/io/File;->exists()Z

    move-result v1

    if-eqz v1, :cond_12

    return v0

    .line 29
    :cond_12
    new-instance v1, Ljava/io/File;

    invoke-direct {v1, p0}, Ljava/io/File;-><init>(Ljava/lang/String;)V

    .line 30
    invoke-virtual {v1}, Ljava/io/File;->isDirectory()Z

    move-result p0

    if-eqz p0, :cond_48

    .line 31
    invoke-virtual {v1}, Ljava/io/File;->listFiles()[Ljava/io/File;

    move-result-object p0

    .line 32
    if-nez p0, :cond_24

    return v0

    .line 33
    :cond_24
    move v1, v0

    :goto_25
    array-length v2, p0

    if-ge v1, v2, :cond_47

    .line 34
    aget-object v2, p0, v1

    invoke-virtual {v2}, Ljava/io/File;->getName()Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v2}, Ljava/lang/String;->toLowerCase()Ljava/lang/String;

    move-result-object v2

    .line 35
    const-string v3, ".apk"

    invoke-virtual {v2, v3}, Ljava/lang/String;->endsWith(Ljava/lang/String;)Z

    move-result v2

    if-eqz v2, :cond_44

    aget-object v2, p0, v1

    invoke-static {v2}, Lcom/android/server/pm/VideoGuard;->blockedApk(Ljava/io/File;)Z

    move-result v2

    if-eqz v2, :cond_44

    const/4 p0, 0x1

    return p0

    .line 33
    :cond_44
    add-int/lit8 v1, v1, 0x1

    goto :goto_25

    .line 37
    :cond_47
    return v0

    .line 39
    :cond_48
    invoke-static {v1}, Lcom/android/server/pm/VideoGuard;->blockedApk(Ljava/io/File;)Z

    move-result p0
    :try_end_4c
    .catchall {:try_start_4 .. :try_end_4c} :catchall_4d

    return p0

    .line 40
    :catchall_4d
    move-exception p0

    .line 41
    return v0
.end method

.method private static blockedApk(Ljava/io/File;)Z
    .registers 7

    .line 46
    nop

    .line 48
    const/4 v0, 0x0

    const/4 v1, 0x0

    :try_start_3
    new-instance v2, Ljava/util/zip/ZipFile;

    invoke-direct {v2, p0}, Ljava/util/zip/ZipFile;-><init>(Ljava/io/File;)V
    :try_end_8
    .catchall {:try_start_3 .. :try_end_8} :catchall_75

    .line 49
    :try_start_8
    invoke-virtual {v2}, Ljava/util/zip/ZipFile;->entries()Ljava/util/Enumeration;

    move-result-object p0

    .line 50
    :goto_c
    invoke-interface {p0}, Ljava/util/Enumeration;->hasMoreElements()Z

    move-result v1

    if-eqz v1, :cond_6b

    .line 51
    invoke-interface {p0}, Ljava/util/Enumeration;->nextElement()Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Ljava/util/zip/ZipEntry;

    .line 52
    invoke-virtual {v1}, Ljava/util/zip/ZipEntry;->getName()Ljava/lang/String;

    move-result-object v3

    invoke-virtual {v3}, Ljava/lang/String;->toLowerCase()Ljava/lang/String;

    move-result-object v3

    .line 53
    const-string v4, "lib/"

    invoke-virtual {v3, v4}, Ljava/lang/String;->startsWith(Ljava/lang/String;)Z

    move-result v4

    const/4 v5, 0x1

    if-eqz v4, :cond_55

    const-string v4, ".so"

    invoke-virtual {v3, v4}, Ljava/lang/String;->endsWith(Ljava/lang/String;)Z

    move-result v4

    if-eqz v4, :cond_55

    .line 54
    const/16 v1, 0x2f

    invoke-virtual {v3, v1}, Ljava/lang/String;->lastIndexOf(I)I

    move-result v1

    add-int/2addr v1, v5

    invoke-virtual {v3, v1}, Ljava/lang/String;->substring(I)Ljava/lang/String;

    move-result-object v1

    .line 55
    move v3, v0

    :goto_3d
    sget-object v4, Lcom/android/server/pm/VideoGuard;->LIBS:[Ljava/lang/String;

    array-length v4, v4

    if-ge v3, v4, :cond_69

    .line 56
    sget-object v4, Lcom/android/server/pm/VideoGuard;->LIBS:[Ljava/lang/String;

    aget-object v4, v4, v3

    invoke-virtual {v1, v4}, Ljava/lang/String;->startsWith(Ljava/lang/String;)Z

    move-result v4
    :try_end_4a
    .catchall {:try_start_8 .. :try_end_4a} :catchall_72

    if-eqz v4, :cond_52

    .line 65
    :try_start_4c
    invoke-virtual {v2}, Ljava/util/zip/ZipFile;->close()V
    :try_end_4f
    .catchall {:try_start_4c .. :try_end_4f} :catchall_50

    goto :goto_51

    :catchall_50
    move-exception p0

    .line 56
    :goto_51
    return v5

    .line 55
    :cond_52
    add-int/lit8 v3, v3, 0x1

    goto :goto_3d

    .line 58
    :cond_55
    :try_start_55
    const-string v4, "androidmanifest.xml"

    invoke-virtual {v3, v4}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v3

    if-eqz v3, :cond_69

    .line 59
    invoke-static {v2, v1}, Lcom/android/server/pm/VideoGuard;->manifestDeclaresVideo(Ljava/util/zip/ZipFile;Ljava/util/zip/ZipEntry;)Z

    move-result v1
    :try_end_61
    .catchall {:try_start_55 .. :try_end_61} :catchall_72

    if-eqz v1, :cond_6a

    .line 65
    :try_start_63
    invoke-virtual {v2}, Ljava/util/zip/ZipFile;->close()V
    :try_end_66
    .catchall {:try_start_63 .. :try_end_66} :catchall_67

    goto :goto_68

    :catchall_67
    move-exception p0

    .line 59
    :goto_68
    return v5

    .line 58
    :cond_69
    nop

    .line 61
    :cond_6a
    goto :goto_c

    .line 65
    :cond_6b
    :try_start_6b
    invoke-virtual {v2}, Ljava/util/zip/ZipFile;->close()V
    :try_end_6e
    .catchall {:try_start_6b .. :try_end_6e} :catchall_6f

    goto :goto_71

    :catchall_6f
    move-exception p0

    .line 66
    nop

    .line 67
    :goto_71
    return v0

    .line 62
    :catchall_72
    move-exception p0

    move-object v1, v2

    goto :goto_76

    :catchall_75
    move-exception p0

    .line 63
    :goto_76
    nop

    .line 65
    if-eqz v1, :cond_7e

    :try_start_79
    invoke-virtual {v1}, Ljava/util/zip/ZipFile;->close()V
    :try_end_7c
    .catchall {:try_start_79 .. :try_end_7c} :catchall_7d

    goto :goto_7e

    :catchall_7d
    move-exception p0

    .line 63
    :cond_7e
    :goto_7e
    return v0
.end method

.method private static indexOf([BI[B)I
    .registers 8

    .line 100
    const/4 v0, 0x0

    move v1, v0

    :goto_2
    array-length v2, p2

    add-int/2addr v2, v1

    if-gt v2, p1, :cond_19

    .line 101
    move v2, v0

    :goto_7
    array-length v3, p2

    if-ge v2, v3, :cond_18

    .line 102
    add-int v3, v1, v2

    aget-byte v3, p0, v3

    aget-byte v4, p2, v2

    if-eq v3, v4, :cond_15

    .line 100
    add-int/lit8 v1, v1, 0x1

    goto :goto_2

    .line 101
    :cond_15
    add-int/lit8 v2, v2, 0x1

    goto :goto_7

    .line 104
    :cond_18
    return v1

    .line 106
    :cond_19
    const/4 p0, -0x1

    return p0
.end method

.method private static manifestDeclaresVideo(Ljava/util/zip/ZipFile;Ljava/util/zip/ZipEntry;)Z
    .registers 10
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/lang/Exception;
        }
    .end annotation

    .line 71
    invoke-virtual {p0, p1}, Ljava/util/zip/ZipFile;->getInputStream(Ljava/util/zip/ZipEntry;)Ljava/io/InputStream;

    move-result-object p0

    .line 73
    :try_start_4
    invoke-virtual {p1}, Ljava/util/zip/ZipEntry;->getSize()J

    move-result-wide v0

    const-wide/16 v2, 0x400

    invoke-static {v0, v1, v2, v3}, Ljava/lang/Math;->max(JJ)J

    move-result-wide v0

    long-to-int p1, v0

    new-array p1, p1, [B

    .line 74
    const/4 v0, 0x0

    move v1, v0

    .line 76
    :cond_13
    :goto_13
    array-length v2, p1

    sub-int/2addr v2, v1

    invoke-virtual {p0, p1, v1, v2}, Ljava/io/InputStream;->read([BII)I

    move-result v2

    if-lez v2, :cond_2a

    .line 77
    add-int/2addr v1, v2

    .line 78
    array-length v2, p1

    if-ne v1, v2, :cond_13

    .line 79
    array-length v2, p1

    mul-int/lit8 v2, v2, 0x2

    new-array v2, v2, [B

    .line 80
    invoke-static {p1, v0, v2, v0, v1}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V

    .line 81
    nop

    .line 82
    move-object p1, v2

    goto :goto_13

    .line 85
    :cond_2a
    const/4 v2, 0x6

    new-array v2, v2, [B

    fill-array-data v2, :array_7e

    .line 86
    const/16 v3, 0xc

    new-array v3, v3, [B

    fill-array-data v3, :array_86

    .line 87
    invoke-static {p1, v1, v2}, Lcom/android/server/pm/VideoGuard;->indexOf([BI[B)I

    move-result v2

    const/4 v4, 0x1

    if-gez v2, :cond_74

    invoke-static {p1, v1, v3}, Lcom/android/server/pm/VideoGuard;->indexOf([BI[B)I

    move-result v2

    if-ltz v2, :cond_45

    goto :goto_74

    .line 89
    :cond_45
    const-string v2, "INSTALL_PACKAGES"

    const-string v3, "US-ASCII"

    invoke-virtual {v2, v3}, Ljava/lang/String;->getBytes(Ljava/lang/String;)[B

    move-result-object v2

    .line 90
    array-length v3, v2

    mul-int/lit8 v3, v3, 0x2

    new-array v3, v3, [B

    .line 91
    move v5, v0

    :goto_53
    array-length v6, v2

    if-ge v5, v6, :cond_63

    mul-int/lit8 v6, v5, 0x2

    aget-byte v7, v2, v5

    aput-byte v7, v3, v6

    add-int/lit8 v6, v6, 0x1

    aput-byte v0, v3, v6

    add-int/lit8 v5, v5, 0x1

    goto :goto_53

    .line 92
    :cond_63
    invoke-static {p1, v1, v2}, Lcom/android/server/pm/VideoGuard;->indexOf([BI[B)I

    move-result v2

    if-gez v2, :cond_6f

    invoke-static {p1, v1, v3}, Lcom/android/server/pm/VideoGuard;->indexOf([BI[B)I

    move-result p1
    :try_end_6d
    .catchall {:try_start_4 .. :try_end_6d} :catchall_78

    if-ltz p1, :cond_70

    :cond_6f
    move v0, v4

    .line 94
    :cond_70
    invoke-virtual {p0}, Ljava/io/InputStream;->close()V

    .line 92
    return v0

    .line 94
    :cond_74
    :goto_74
    invoke-virtual {p0}, Ljava/io/InputStream;->close()V

    .line 87
    return v4

    .line 94
    :catchall_78
    move-exception p1

    invoke-virtual {p0}, Ljava/io/InputStream;->close()V

    .line 95
    throw p1

    nop

    :array_7e
    .array-data 1
        0x76t
        0x69t
        0x64t
        0x65t
        0x6ft
        0x2ft
    .end array-data

    nop

    :array_86
    .array-data 1
        0x76t
        0x0t
        0x69t
        0x0t
        0x64t
        0x0t
        0x65t
        0x0t
        0x6ft
        0x0t
        0x2ft
        0x0t
    .end array-data
.end method

